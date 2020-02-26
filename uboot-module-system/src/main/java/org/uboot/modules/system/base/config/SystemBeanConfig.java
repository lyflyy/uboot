package org.uboot.modules.system.base.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.uboot.modules.system.service.ISysUserService;
import org.uboot.modules.system.service.impl.SysUserServiceImpl;

/**
 * @author: LiYang
 * @Email: lyflyyvip@163.com
 * @create: 2020-02-26 12:11
 * @Description:
 * 可以让其他项目重写该bean，重新实现该service中的某些方法
 **/
@Configuration
public class SystemBeanConfig {

    @Bean("sysUserServiceImpl")
    @ConditionalOnMissingBean
    public ISysUserService initSysUserservice(){
        return new SysUserServiceImpl();
    }

}
