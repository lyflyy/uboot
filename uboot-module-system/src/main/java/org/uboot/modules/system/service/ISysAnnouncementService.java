package org.uboot.modules.system.service;

import org.uboot.modules.system.entity.SysAnnouncement;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 系统通告表
 * @Author: jeecg-boot
 * @Date:  2019-01-02
 * @Version: V1.0
 */
public interface ISysAnnouncementService extends IService<SysAnnouncement> {

	/**
	 * 根据 模板发送消息
	 *
	 * @param templateCode 模板编号
	 * @param parameters 可变长参数, 参数 至少 包含两个第一个 是 通知对象，第二个是 发送人
	 */
	void sendWithTemplate(String templateCode, String ...parameters);

	boolean sendAnnouncement(SysAnnouncement sysAnnouncement, String currentUserName);

	public void saveAnnouncement(SysAnnouncement sysAnnouncement);

	public boolean upDateAnnouncement(SysAnnouncement sysAnnouncement);

	public void saveSysAnnouncement(String title, String msgContent);

	public Page<SysAnnouncement> querySysCementPageByUserId(Page<SysAnnouncement> page, String userId, String msgCategory);


}
