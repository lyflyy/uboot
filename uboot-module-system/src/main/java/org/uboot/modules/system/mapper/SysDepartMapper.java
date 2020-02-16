package org.uboot.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.uboot.common.aspect.annotation.PermissionData;
import org.uboot.config.mybatis.permission.annotation.DepartPermission;
import org.uboot.modules.system.entity.SysDepart;
import org.uboot.modules.system.model.SysDepartTreeModel;
import org.uboot.modules.system.model.SysDepartTreeWithManagerModel;
import org.uboot.modules.system.model.TreeModel;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * <p>
 * 部门 Mapper 接口
 * <p>
 *
 * @Author: Steve
 * @Since：   2019-01-22
 */
public interface SysDepartMapper extends BaseMapper<SysDepart> {

	/**
	 * 根据用户ID查询部门集合
	 */
	public List<SysDepart> queryUserDeparts(@Param("userId") String userId);

    /**
     * 根据租户id查询部门集合
     * @param userId
     * @param tenantId
     * @return
     */
    List<SysDepart> queryUserDepartsByTenantId(@Param("userId")String userId,@Param("tenantId") String tenantId);

	/**
	 * 根据用户名查询部门
	 *
	 * @param username
	 * @return
	 */
	public List<SysDepart> queryDepartsByUsername(@Param("username") String username);

	@Select("select id from sys_depart where org_code=#{orgCode}")
	public String queryDepartIdByOrgCode(@Param("orgCode") String orgCode);

	@Select("select id,parent_id from sys_depart where id=#{departId}")
	public SysDepart getParentDepartId(@Param("departId") String departId);

    @Select("SELECT*FROM sys_depart WHERE del_flag=0")
    List<SysDepart> selectAll();


    /**
     * 根据当前登陆的用户的orgcode 获取当前用户所属部别以及所有子部别
     * @param id
     * @return
     */
    List<SysDepartTreeWithManagerModel> selectAllDepartByUser(@Param("id") String id);


    /**
     * 根据parent_id 获取部别
     * mybatis 递归
     * @param id
     * @return
     */
    List<SysDepartTreeModel> selectByParentId(@Param("id") String id);

}
