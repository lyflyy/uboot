package org.uboot.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.uboot.modules.system.entity.SysDepart;
import org.uboot.modules.system.entity.SysUser;
import org.uboot.modules.system.model.SysDepartModel;
import org.uboot.modules.system.model.SysDepartTreeModel;
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
    List<SysDepartModel> selectAllDepartByUser(@Param("id") String id);

    List<SysDepartModel> selectAllDepart();


    /**
     * 根据parent_id 获取部别
     * mybatis 递归
     * @param id
     * @return
     */
    List<SysDepartTreeModel> selectByParentId(@Param("id") String id);


    /**
     * 根据部别id，查询管理员用户信息
     * @param id
     * @return
     */
    List<SysUser> selectManagersByDepartId(@Param("id") String id);

    /**
     * 查询部门人数
     * @param id
     * @return
     */
    int selectDepartUserCount(@Param("id") String id);

    /**
     * 根据部门id与用户id列表，批量删除用户与部门的关系
     * @param userIds
     * @return
     */
    int deleteDepartUsers(@Param("userIds") List<String> userIds);

    List<String> queryDepartsByUserId(@Param("userId") String userId);

}
