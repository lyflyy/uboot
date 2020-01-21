package com.uyibai.uboot.service.impl;

import com.uyibai.uboot.persistent.entity.SysUserOnline;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.uyibai.uboot.persistent.mapper.SysUserOnlineMapper;
import com.uyibai.uboot.service.ISysUserOnlineService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 在线用户记录 服务实现类
 * </p>
 *
 * @author LiYang [lyflyyvip@163.com]
 * @since 2020-01-17
 */
@Service
public class SysUserOnlineServiceImpl extends ServiceImpl<SysUserOnlineMapper, SysUserOnline> implements ISysUserOnlineService {

    /**
     * 通过会话序号查询信息
     *
     * @param sessionId 会话ID
     * @return 在线用户信息
     */
    @Override
    public SysUserOnline selectOnlineById(String sessionId)
    {
        return baseMapper.selectOnlineById(sessionId);
    }

}
