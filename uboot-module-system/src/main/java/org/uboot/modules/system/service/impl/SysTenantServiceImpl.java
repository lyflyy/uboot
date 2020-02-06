package org.uboot.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.uboot.modules.system.entity.SysTenant;
import org.uboot.modules.system.entity.SysTenantUser;
import org.uboot.modules.system.mapper.SysTenantMapper;
import org.uboot.modules.system.mapper.SysTenantUserMapper;
import org.uboot.modules.system.service.ISysTenantService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description: 租户
 * @Author: jeecg-boot
 * @Date:   2020-02-03
 * @Version: V1.0
 */
@Service
public class SysTenantServiceImpl extends ServiceImpl<SysTenantMapper, SysTenant> implements ISysTenantService {

    @Override
    public List<SysTenant> getByUserId(String userId) {
        return baseMapper.selectByUserId(userId);
    }

    @Override
    public SysTenant getLastOne() {
        return baseMapper.selectLastByCreateTime();
    }

    @Override
    public String getLastOneCode() {
        SysTenant last = getLastOne();
        if(last != null){
            return last.getTenantCode();
        }
        return null;
    }

}
