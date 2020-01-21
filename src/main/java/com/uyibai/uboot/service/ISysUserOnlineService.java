package com.uyibai.uboot.service;

import com.uyibai.uboot.persistent.entity.SysUserOnline;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 在线用户记录 服务类
 * </p>
 *
 * @author LiYang [lyflyyvip@163.com]
 * @since 2020-01-17
 */
public interface ISysUserOnlineService extends IService<SysUserOnline> {

    SysUserOnline selectOnlineById(String valueOf);

}
