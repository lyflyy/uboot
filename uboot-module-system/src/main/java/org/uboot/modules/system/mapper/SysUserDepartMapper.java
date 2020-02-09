package org.uboot.modules.system.mapper;

import java.util.List;

import org.uboot.modules.system.entity.SysUserDepart;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import io.lettuce.core.dynamic.annotation.Param;

public interface SysUserDepartMapper extends BaseMapper<SysUserDepart>{

	List<SysUserDepart> getUserDepartByUid(@Param("userId") String userId);

    int deleteByDepCodeAndUser(@Param("deptCode")String deptCode,@Param("userId") String userId);
}
