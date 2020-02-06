package org.uboot.modules.system.service;

import org.uboot.modules.system.entity.SysTenant;
import com.baomidou.mybatisplus.extension.service.IService;
import org.uboot.modules.system.entity.SysTenantUser;

import java.util.List;

/**
 * @Description: 租户
 * @Author: jeecg-boot
 * @Date:   2020-02-03
 * @Version: V1.0
 */
public interface ISysTenantService extends IService<SysTenant> {

    List<SysTenant> getByUserId(String userId);

    /**
     * 根据创建时间获取最后一个租户
     * @return
     */
    SysTenant getLastOne();

    String getLastOneCode();
}
