package org.uboot.modules.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.uboot.modules.system.entity.SysTenant;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.uboot.modules.system.entity.SysTenantUser;

/**
 * @Description: 租户
 * @Author: jeecg-boot
 * @Date:   2020-02-03
 * @Version: V1.0
 */
public interface SysTenantMapper extends BaseMapper<SysTenant> {

    List<SysTenant> selectByUserId(@Param("userId") String userId);
}
