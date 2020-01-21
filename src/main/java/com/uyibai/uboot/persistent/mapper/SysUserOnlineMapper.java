package com.uyibai.uboot.persistent.mapper;

import com.uyibai.uboot.persistent.entity.SysUserOnline;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 在线用户记录 Mapper 接口
 * </p>
 *
 * @author LiYang [lyflyyvip@163.com]
 * @since 2020-01-17
 */
public interface SysUserOnlineMapper extends BaseMapper<SysUserOnline> {

    SysUserOnline selectOnlineById(String sessionId);
}
