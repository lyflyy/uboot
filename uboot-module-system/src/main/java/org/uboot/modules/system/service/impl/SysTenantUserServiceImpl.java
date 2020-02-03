package org.uboot.modules.system.service.impl;

import org.uboot.modules.system.entity.SysTenantUser;
import org.uboot.modules.system.mapper.SysTenantUserMapper;
import org.uboot.modules.system.service.ISysTenantUserService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 租户-用户
 * @Author: jeecg-boot
 * @Date:   2020-02-03
 * @Version: V1.0
 */
@Service
public class SysTenantUserServiceImpl extends ServiceImpl<SysTenantUserMapper, SysTenantUser> implements ISysTenantUserService {

}
