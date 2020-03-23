package org.uboot.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.lettuce.core.dynamic.annotation.Param;
import org.uboot.modules.system.entity.SysUserDepart;

import java.util.List;

public interface SysUserDepartMapper extends BaseMapper<SysUserDepart>{

	List<SysUserDepart> getUserDepartByUid(@Param("userId") String userId);

    SysUserDepart getDepartAdminByDepId(@Param("depId") String depId);

    int deleteByDepCodeAndUser(@Param("deptCode")String deptCode,@Param("userId") String userId);
}
