package org.uboot.modules.system.mapper;

import org.springframework.data.repository.query.Param;
import org.uboot.modules.system.entity.SysRole;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 角色表 Mapper 接口
 * </p>
 *
 * @Author scott
 * @since 2018-12-19
 */
public interface SysRoleMapper extends BaseMapper<SysRole> {

    int deleteRoleUsers(List<String> userIds);
}
