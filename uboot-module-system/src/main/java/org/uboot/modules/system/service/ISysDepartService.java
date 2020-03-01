package org.uboot.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.web.multipart.MultipartFile;
import org.uboot.modules.system.entity.SysDepart;
import org.uboot.modules.system.model.DepartIdModel;
import org.uboot.modules.system.model.SysDepartModel;
import org.uboot.modules.system.model.SysDepartTreeModel;
import org.uboot.modules.system.vo.SysDepartManagersVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 部门表 服务实现类
 * <p>
 *
 * @Author:Steve
 * @Since：   2019-01-22
 */
public interface ISysDepartService extends IService<SysDepart>{


    /**
     * 查询所有部门信息,并分节点进行显示
     * @return
     */
    List<SysDepartTreeModel> queryTreeList();

    List<SysDepart> queryList();

    /**
     * 查询所有部门DepartId信息,并分节点进行显示
     * @return
     */
    public List<DepartIdModel> queryDepartIdTreeList();

    /**
     * 保存部门数据
     * @param sysDepart
     */
    void saveDepartData(SysDepart sysDepart, String username);

    /**
     * 更新depart数据
     * @param sysDepart
     * @return
     */
    Boolean updateDepartDataById(SysDepart sysDepart, String username);

    /**
     * 删除depart数据
     * @param id
     * @return
     */
	/* boolean removeDepartDataById(String id); */

    /**
     * 根据关键字搜索相关的部门数据
     * @param keyWord
     * @return
     */
    List<SysDepartTreeModel> searhBy(String keyWord);

    /**
     * 根据部门id删除并删除其可能存在的子级部门
     * @param id
     * @return
     */
    boolean delete(String id);

    /**
     * 查询SysDepart集合
     * @param userId
     * @return
     */
	public List<SysDepart> queryUserDeparts(String userId);


    /**
     * 根据租户id查询部门集合
     * @param userId
     * @return
     */
	public List<SysDepart> queryUserDepartsByTenantId(String userId, String tenantId);

    /**
     * 根据部门id 获取上级部门
     *
     * @param sysCode
     * @return
     */
    List<SysDepart> queryParents(String sysCode);

    /**
     * 根据用户名查询部门
     *
     * @param username
     * @return
     */
    List<SysDepart> queryDepartsByUsername(String username);

	 /**
     * 根据部门id批量删除并删除其可能存在的子级部门
     * @param id
     * @return
     */
	void deleteBatchWithChildren(List<String> ids);


    /**
     * 根据当前登陆的用户的orgcode 获取当前用户所属部别以及所有子部别
     * @param id
     * @return
     */
    List<SysDepartModel> getByUser(String id);

    List<SysDepartModel> getAll();

    /**
     * 处理部门新增的时候管理员信息
     * 处理 管理员与部门的关系
     * 处理 管理员与角色的关系
     * @param sysDepart
     */
    void toProcessAddMangers(SysDepartManagersVO sysDepart);

    void toProcessUpdateMangers(SysDepartManagersVO sysDepart);

    List<String> queryDepartsByUserId(String userId);

    /**
     * 根据部别name去查询部别id
     * @param sql
     * @return
     */
    String findParentIdByName(String name, String sql);

    int importDepart(HttpServletRequest request, MultipartFile file, ImportParams params) throws Exception;
}
