package org.uboot.modules.system.service.impl;

import cn.hutool.core.util.ArrayUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.uboot.common.constant.CommonConstant;
import org.uboot.common.constant.CommonSendStatus;
import org.uboot.common.util.oConvertUtils;
import org.uboot.modules.message.entity.SysMessageTemplate;
import org.uboot.modules.message.service.ISysMessageTemplateService;
import org.uboot.modules.message.websocket.WebSocket;
import org.uboot.modules.system.entity.SysAnnouncement;
import org.uboot.modules.system.entity.SysAnnouncementSend;
import org.uboot.modules.system.mapper.SysAnnouncementMapper;
import org.uboot.modules.system.mapper.SysAnnouncementSendMapper;
import org.uboot.modules.system.service.ISysAnnouncementService;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @Description: 系统通告表
 * @Author: jeecg-boot
 * @Date: 2019-01-02
 * @Version: V1.0
 */
@Service
@Slf4j
public class SysAnnouncementServiceImpl extends ServiceImpl<SysAnnouncementMapper, SysAnnouncement> implements ISysAnnouncementService {

    @Resource
    private SysAnnouncementMapper sysAnnouncementMapper;

    @Resource
    private SysAnnouncementSendMapper sysAnnouncementSendMapper;

    @Resource
    private ISysMessageTemplateService sysMessageTemplateService;

    @Resource
    private WebSocket webSocket;

    /**
     * 根据 模板发送消息
     *
     * @param templateCode 模板编号
     * @param parameters   可变长参数
     */
    @Override
    @Transactional
    public void sendWithTemplate(String templateCode, String... parameters) {
        log.info("sendWithTemplateParam:{},{}", templateCode, parameters);
        if (parameters.length < 2) {
            return;
        }
        // 通知对象
        String userIds = parameters[0];
        // 发送人
        String senderUser = parameters[1];
        SysMessageTemplate sysMessageTemplate = sysMessageTemplateService.selectByCode(templateCode);
        SysAnnouncement sysAnnouncement = SysAnnouncement.convert(sysMessageTemplate, ArrayUtil.sub(parameters, 2, parameters.length));
	    sysAnnouncement.setMsgType(CommonConstant.MSG_TYPE_UESR);
        sysAnnouncement.setUserIds(userIds);
        this.saveAnnouncement(sysAnnouncement);
        // 将通知发送出去
        this.sendAnnouncement(sysAnnouncement, senderUser);
    }


    /**
     * 发送消息
     *
     * @param sysAnnouncement
     * @param currentUserName
     * @return
     */
    @Override
    public boolean sendAnnouncement(SysAnnouncement sysAnnouncement, String currentUserName) {
        sysAnnouncement.setSendStatus(CommonSendStatus.PUBLISHED_STATUS_1);//发布中
        sysAnnouncement.setSendTime(new Date());
        sysAnnouncement.setSender(currentUserName);
        boolean ok = updateById(sysAnnouncement);
        if (ok) {
            if (CommonConstant.MSG_TYPE_ALL.equals(sysAnnouncement.getMsgType())) {
                JSONObject obj = new JSONObject();
                obj.put("cmd", "topic");
                obj.put("msgId", sysAnnouncement.getId());
                obj.put("msgTxt", sysAnnouncement.getTitile());
                webSocket.sendAllMessage(obj.toJSONString());
            } else {
                // 2.插入用户通告阅读标记表记录
                String userId = sysAnnouncement.getUserIds();
                String[] userIds = userId.substring(0, (userId.length() - 1)).split(",");
                String anntId = sysAnnouncement.getId();
                Date refDate = new Date();
                JSONObject obj = new JSONObject();
                obj.put("cmd", "user");
                obj.put("msgId", sysAnnouncement.getId());
                obj.put("msgTxt", sysAnnouncement.getTitile());
                webSocket.sendMoreMessage(userIds, obj.toJSONString());
            }
        }
        return ok;
    }
	
    @Transactional
    @Override
    public void saveAnnouncement(SysAnnouncement sysAnnouncement) {
        if (CommonConstant.MSG_TYPE_ALL.equals(sysAnnouncement.getMsgType())) {
            sysAnnouncementMapper.insert(sysAnnouncement);
        } else {
            // 1.插入通告表记录
            sysAnnouncementMapper.insert(sysAnnouncement);
            // 2.插入用户通告阅读标记表记录
            String userId = sysAnnouncement.getUserIds();
            String[] userIds = userId.split(",");
            String anntId = sysAnnouncement.getId();
            Date refDate = new Date();
            for (int i = 0; i < userIds.length; i++) {
            	if(StringUtils.isNoneBlank(userIds[i])){
		            SysAnnouncementSend announcementSend = new SysAnnouncementSend();
		            announcementSend.setAnntId(anntId);
		            announcementSend.setUserId(userIds[i]);
		            announcementSend.setReadFlag(CommonConstant.NO_READ_FLAG);
		            announcementSend.setReadTime(refDate);
		            sysAnnouncementSendMapper.insert(announcementSend);
	            }
            }
        }
    }

    /**
     * @功能：编辑消息信息
     */
    @Transactional
    @Override
    public boolean upDateAnnouncement(SysAnnouncement sysAnnouncement) {
        // 1.更新系统信息表数据
        sysAnnouncementMapper.updateById(sysAnnouncement);
        String userId = sysAnnouncement.getUserIds();
        if (oConvertUtils.isNotEmpty(userId) && CommonConstant.MSG_TYPE_UESR.equals(sysAnnouncement.getMsgType())) {
            // 2.补充新的通知用户数据
            String[] userIds = userId.substring(0, (userId.length() - 1)).split(",");
            String anntId = sysAnnouncement.getId();
            Date refDate = new Date();
            for (int i = 0; i < userIds.length; i++) {
                LambdaQueryWrapper<SysAnnouncementSend> queryWrapper = new LambdaQueryWrapper<SysAnnouncementSend>();
                queryWrapper.eq(SysAnnouncementSend::getAnntId, anntId);
                queryWrapper.eq(SysAnnouncementSend::getUserId, userIds[i]);
                List<SysAnnouncementSend> announcementSends = sysAnnouncementSendMapper.selectList(queryWrapper);
                if (announcementSends.size() <= 0) {
                    SysAnnouncementSend announcementSend = new SysAnnouncementSend();
                    announcementSend.setAnntId(anntId);
                    announcementSend.setUserId(userIds[i]);
                    announcementSend.setReadFlag(CommonConstant.NO_READ_FLAG);
                    announcementSend.setReadTime(refDate);
                    sysAnnouncementSendMapper.insert(announcementSend);
                }
            }
            // 3. 删除多余通知用户数据
            Collection<String> delUserIds = Arrays.asList(userIds);
            LambdaQueryWrapper<SysAnnouncementSend> queryWrapper = new LambdaQueryWrapper<SysAnnouncementSend>();
            queryWrapper.notIn(SysAnnouncementSend::getUserId, delUserIds);
            queryWrapper.eq(SysAnnouncementSend::getAnntId, anntId);
            sysAnnouncementSendMapper.delete(queryWrapper);
        }
        return true;
    }

    // @功能：流程执行完成保存消息通知
    @Override
    public void saveSysAnnouncement(String title, String msgContent) {
        SysAnnouncement announcement = new SysAnnouncement();
        announcement.setTitile(title);
        announcement.setMsgContent(msgContent);
        announcement.setSender("JEECG BOOT");
        announcement.setPriority(CommonConstant.PRIORITY_L);
        announcement.setMsgType(CommonConstant.MSG_TYPE_ALL);
        announcement.setSendStatus(CommonConstant.HAS_SEND);
        announcement.setSendTime(new Date());
        announcement.setDelFlag(CommonConstant.DEL_FLAG_0.toString());
        sysAnnouncementMapper.insert(announcement);
    }

    @Override
    public Page<SysAnnouncement> querySysCementPageByUserId(Page<SysAnnouncement> page, String userId, String msgCategory) {
        return page.setRecords(sysAnnouncementMapper.querySysCementListByUserId(page, userId, msgCategory));
    }

}
