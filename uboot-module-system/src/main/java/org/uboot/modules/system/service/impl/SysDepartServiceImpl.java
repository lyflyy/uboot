package org.uboot.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.netty.util.internal.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.uboot.common.constant.CacheConstant;
import static org.uboot.common.constant.CacheKeyConstant.CACHE_DEPART_ROLE_CODE;
import static org.uboot.common.constant.CacheKeyConstant.CACHE_DEPART_ROLE_KEY;
import org.uboot.common.constant.CommonConstant;
import org.uboot.common.exception.UBootException;
import org.uboot.common.util.YouBianCodeUtil;
import org.uboot.modules.system.entity.SysDepart;
import org.uboot.modules.system.entity.SysRole;
import org.uboot.modules.system.entity.SysUserDepart;
import org.uboot.modules.system.entity.SysUserRole;
import org.uboot.modules.system.mapper.SysDepartMapper;
import org.uboot.modules.system.mapper.SysUserMapper;
import org.uboot.modules.system.model.DepartIdModel;
import org.uboot.modules.system.model.SysDepartModel;
import org.uboot.modules.system.model.SysDepartTreeModel;
import org.uboot.modules.system.service.ISysDepartService;
import org.uboot.modules.system.service.ISysDictService;
import org.uboot.modules.system.service.ISysRoleService;
import org.uboot.modules.system.service.ISysUserDepartService;
import org.uboot.modules.system.service.ISysUserRoleService;
import org.uboot.modules.system.util.FindsDepartsChildrenUtil;
import org.uboot.modules.system.vo.SysDepartManagersVO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * <p>
 * 部门表 服务实现类
 * <p>
 *
 * @Author Steve
 * @Since 2019-01-22
 */
@Service
public class SysDepartServiceImpl extends ServiceImpl<SysDepartMapper, SysDepart> implements ISysDepartService {

    @Autowired
    private ISysUserRoleService sysUserRoleService;

    @Autowired
    private ISysUserDepartService sysUserDepartService;

    @Autowired
    private ISysDictService sysDictService;

    @Autowired
    private ISysRoleService sysRoleService;

    @Autowired
    private SysUserMapper sysUserMapper;

	/**
	 * queryTreeList 对应 queryTreeList 查询所有的部门数据,以树结构形式响应给前端
	 */
	@Cacheable(value = CacheConstant.SYS_DEPARTS_CACHE)
	@Override
	public List<SysDepartTreeModel> queryTreeList() {
		LambdaQueryWrapper<SysDepart> query = new LambdaQueryWrapper<SysDepart>();
		query.eq(SysDepart::getDelFlag, CommonConstant.DEL_FLAG_0.toString());
		query.orderByAsc(SysDepart::getDepartOrder);
		List<SysDepart> list = this.list(query);
		// 调用wrapTreeDataToTreeList方法生成树状数据
		List<SysDepartTreeModel> listResult = FindsDepartsChildrenUtil.wrapTreeDataToTreeList(list);
		return listResult;
	}

    @Override
    public List<SysDepart> queryList() {
        return baseMapper.selectAll();
    }

    @Cacheable(value = CacheConstant.SYS_DEPART_IDS_CACHE)
	@Override
	public List<DepartIdModel> queryDepartIdTreeList() {
		LambdaQueryWrapper<SysDepart> query = new LambdaQueryWrapper<SysDepart>();
		query.eq(SysDepart::getDelFlag, CommonConstant.DEL_FLAG_0.toString());
		query.orderByAsc(SysDepart::getDepartOrder);
		List<SysDepart> list = this.list(query);
		// 调用wrapTreeDataToTreeList方法生成树状数据
		List<DepartIdModel> listResult = FindsDepartsChildrenUtil.wrapTreeDataToDepartIdTreeList(list);
		return listResult;
	}

	/**
	 * saveDepartData 对应 add 保存用户在页面添加的新的部门对象数据
	 */
	@Override
	@Transactional
	public void saveDepartData(SysDepart sysDepart, String username) {
		if (sysDepart != null && username != null) {
			if (sysDepart.getParentId() == null) {
				sysDepart.setParentId("");
			}
			String s = UUID.randomUUID().toString().replace("-", "");
			sysDepart.setId(s);
			// 先判断该对象有无父级ID,有则意味着不是最高级,否则意味着是最高级
			// 获取父级ID
			String parentId = sysDepart.getParentId();
			String[] codeArray = generateOrgCode(parentId);
			sysDepart.setOrgCode(codeArray[0]);
			String orgType = codeArray[1];
			sysDepart.setOrgType(String.valueOf(orgType));
			sysDepart.setCreateTime(new Date());
			sysDepart.setDelFlag(CommonConstant.DEL_FLAG_0.toString());
			this.save(sysDepart);
		}

	}

	/**
	 * saveDepartData 的调用方法,生成部门编码和部门类型
	 *
	 * @param parentId
	 * @return
	 */
	private String[] generateOrgCode(String parentId) {
		//update-begin--Author:Steve  Date:20190201 for：组织机构添加数据代码调整
				LambdaQueryWrapper<SysDepart> query = new LambdaQueryWrapper<SysDepart>();
				LambdaQueryWrapper<SysDepart> query1 = new LambdaQueryWrapper<SysDepart>();
				String[] strArray = new String[2];
		        // 创建一个List集合,存储查询返回的所有SysDepart对象
		        List<SysDepart> departList = new ArrayList<>();
				// 定义新编码字符串
				String newOrgCode = "";
				// 定义旧编码字符串
				String oldOrgCode = "";
				// 定义部门类型
				String orgType = "";
				// 如果是最高级,则查询出同级的org_code, 调用工具类生成编码并返回
				if (StringUtil.isNullOrEmpty(parentId)) {
					// 线判断数据库中的表是否为空,空则直接返回初始编码
					query1.eq(SysDepart::getParentId, "").or().isNull(SysDepart::getParentId);
					query1.orderByDesc(SysDepart::getOrgCode);
					departList = this.list(query1);
					if(departList == null || departList.size() == 0) {
						strArray[0] = YouBianCodeUtil.getNextYouBianCode(null);
						strArray[1] = "1";
						return strArray;
					}else {
					SysDepart depart = departList.get(0);
					oldOrgCode = depart.getOrgCode();
					orgType = depart.getOrgType();
					newOrgCode = YouBianCodeUtil.getNextYouBianCode(oldOrgCode);
					}
				} else { // 反之则查询出所有同级的部门,获取结果后有两种情况,有同级和没有同级
					// 封装查询同级的条件
					query.eq(SysDepart::getParentId, parentId);
					// 降序排序
					query.orderByDesc(SysDepart::getOrgCode);
					// 查询出同级部门的集合
					List<SysDepart> parentList = this.list(query);
					// 查询出父级部门
					SysDepart depart = this.getById(parentId);
					// 获取父级部门的Code
					String parentCode = depart.getOrgCode();
					// 根据父级部门类型算出当前部门的类型
					orgType = String.valueOf(Integer.valueOf(depart.getOrgType()) + 1);
					// 处理同级部门为null的情况
					if (parentList == null || parentList.size() == 0) {
						// 直接生成当前的部门编码并返回
						newOrgCode = YouBianCodeUtil.getSubYouBianCode(parentCode, null);
					} else { //处理有同级部门的情况
						// 获取同级部门的编码,利用工具类
						String subCode = parentList.get(0).getOrgCode();
						// 返回生成的当前部门编码
						newOrgCode = YouBianCodeUtil.getSubYouBianCode(parentCode, subCode);
					}
				}
				// 返回最终封装了部门编码和部门类型的数组
				strArray[0] = newOrgCode;
				strArray[1] = orgType;
				return strArray;
		//update-end--Author:Steve  Date:20190201 for：组织机构添加数据代码调整
	}


	/**
	 * removeDepartDataById 对应 delete方法 根据ID删除相关部门数据
	 *
	 */
	/*
	 * @Override
	 *
	 * @Transactional public boolean removeDepartDataById(String id) {
	 * System.out.println("要删除的ID 为=============================>>>>>"+id); boolean
	 * flag = this.removeById(id); return flag; }
	 */

	/**
	 * updateDepartDataById 对应 edit 根据部门主键来更新对应的部门数据
	 */
	@Override
	@Transactional
	public Boolean updateDepartDataById(SysDepart sysDepart, String username) {
		if (sysDepart != null && username != null) {
			sysDepart.setUpdateTime(new Date());
			sysDepart.setUpdateBy(username);
			this.updateById(sysDepart);
			return true;
		} else {
			return false;
		}

	}

	@Override
	@Transactional
	public void deleteBatchWithChildren(List<String> ids) {
		List<String> idList = new ArrayList<String>();
		for(String id: ids) {
			idList.add(id);
			this.checkChildrenExists(id, idList);
		}
		this.removeByIds(idList);

	}

    @Override
    public List<SysDepartModel> getByUser(String id) {
        return baseMapper.selectAllDepartByUser(id);
    }

    @Override
    public List<SysDepartModel> getAll() {
        return baseMapper.selectAllDepart();
    }


    /**
     * 处理部门新增的时候管理员信息
     * 处理 管理员与部门的关系
     * 处理 管理员与角色的关系
     *
     * todo - 该操作的前提是一个用户只能属于一个一个部门的前提, 用户属多个部门的情况下该方法需要重新处理
     * @param sysDepart
     */
    @Transactional
    @Override
    public void toProcessAddMangers(SysDepartManagersVO sysDepart) {
        // 处理该用户的角色 新增部门管理员角色，先删除所有用户与部门管理员角色关系，再增加
        String departRoleCode = sysDictService.queryDictValueByKey(CACHE_DEPART_ROLE_CODE, CACHE_DEPART_ROLE_KEY);
        SysRole sysRole = sysRoleService.getOne(new LambdaQueryWrapper<SysRole>().eq(SysRole::getRoleCode, departRoleCode));
        if(sysRole == null) throw new UBootException("部门角色信息为空，请联系管理员！");
        // 所有被选中管理员的用户应该删除该用户与原部门的关系，并新建该用户与该部门的关系，并标示为管理员
        if(sysDepart.getUserId() != null && sysDepart.getUserId().size() > 0){
            baseMapper.deleteDepartUsers(sysDepart.getUserId());
            sysUserRoleService.remove(new LambdaQueryWrapper<SysUserRole>().
                    in(SysUserRole::getUserId, sysDepart.getUserId())
                    .eq(SysUserRole::getRoleId, sysRole.getId())
            );
        }
        processSaveDepartManagers(sysDepart, sysRole.getId());
    }

    /**
     * 处理部门编辑的时候管理员信息
     * 找到原部门的所有管理员
     * 处理 管理员与部门的关系
     * 处理 管理员与角色的关系
     *
     * todo - 该操作的前提是一个用户只能属于一个一个部门的前提, 用户属多个部门的情况下该方法需要重新处理
     * @param sysDepart
     */
    @Transactional
    @Override
    public void toProcessUpdateMangers(SysDepartManagersVO sysDepart) {
        // 从字典中获取部门角色id
        String departRoleCode = sysDictService.queryDictValueByKey(CACHE_DEPART_ROLE_CODE, CACHE_DEPART_ROLE_KEY);
        SysRole sysRole = sysRoleService.getOne(new LambdaQueryWrapper<SysRole>().eq(SysRole::getRoleCode, departRoleCode));
        if(sysRole == null) throw new UBootException("部门角色信息为空，请联系管理员！");
        // 原该部门下的所有管理员
        List<SysUserDepart> oldUsers = sysUserDepartService.list(new LambdaQueryWrapper<SysUserDepart>()
                .eq(SysUserDepart::getIsManager, true)
                .eq(SysUserDepart::getDepId, sysDepart.getId())
        );
        List<String> ids = new ArrayList<>();
        if(oldUsers != null && oldUsers.size() > 0){
            ids.addAll(oldUsers.stream().map(e -> e.getUserId()).collect(Collectors.toList()));
        }
        // 1. 删除这些管理员的角色信息
        ids.addAll(sysDepart.getUserId());
        // 2. 删除老管理员 + 新管理员所有与部门的关系
        baseMapper.deleteDepartUsers(ids);
        sysUserRoleService.remove(new LambdaQueryWrapper<SysUserRole>().
                in(SysUserRole::getUserId, ids)
                .eq(SysUserRole::getRoleId, sysRole.getId())
        );
        processSaveDepartManagers(sysDepart, sysRole.getId());
    }

    @Override
    public List<String> queryDepartsByUserId(String userId) {
        return baseMapper.queryDepartsByUserId(userId);
    }

    private void processSaveDepartManagers(SysDepartManagersVO sysDepart, String roleId){
        // 新增用户与该部门的关系，并标示为管理员
        List<SysUserDepart> userDeparts = new ArrayList<>();
        for (String userId : sysDepart.getUserId()) {
            SysUserDepart sysUserDepart = new SysUserDepart(userId, sysDepart.getId(), true);
            userDeparts.add(sysUserDepart);
        }
        sysUserDepartService.saveBatch(userDeparts);
        List<SysUserRole> roles = new ArrayList<>();
        for (String userId : sysDepart.getUserId()) {
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setUserId(userId);
            sysUserRole.setRoleId(roleId);
            roles.add(sysUserRole);
        }
        sysUserRoleService.saveBatch(roles);
        // 修改所有用户 sys_user 部门code 为当前部门code
        if(sysDepart.getUserId().size() > 0){
            sysUserMapper.updateOrgCodeBatch(sysDepart.getUserId(), sysDepart.getOrgCode());
        }
    }

    /**
	 * <p>
	 * 根据关键字搜索相关的部门数据
	 * </p>
	 */
	@Override
	public List<SysDepartTreeModel> searhBy(String keyWord) {
		LambdaQueryWrapper<SysDepart> query = new LambdaQueryWrapper<SysDepart>();
		query.like(SysDepart::getDepartName, keyWord);
		//update-begin--Author:huangzhilin  Date:20140417 for：[bugfree号]组织机构搜索回显优化--------------------
		SysDepartTreeModel model = new SysDepartTreeModel();
		List<SysDepart> departList = this.list(query);
		List<SysDepartTreeModel> newList = new ArrayList<>();
		if(departList.size() > 0) {
			for(SysDepart depart : departList) {
				model = new SysDepartTreeModel(depart);
				model.setChildren(null);
	    //update-end--Author:huangzhilin  Date:20140417 for：[bugfree号]组织机构搜索功回显优化----------------------
				newList.add(model);
			}
			return newList;
		}
		return null;
	}

	/**
	 * 根据部门id删除并且删除其可能存在的子级任何部门
	 */
	@Override
	public boolean delete(String id) {
		List<String> idList = new ArrayList<>();
		idList.add(id);
		this.checkChildrenExists(id, idList);
		//清空部门树内存
		//FindsDepartsChildrenUtil.clearDepartIdModel();
		boolean ok = this.removeByIds(idList);
		return ok;
	}

	/**
	 * delete 方法调用
	 * @param id
	 * @param idList
	 */
	private void checkChildrenExists(String id, List<String> idList) {
		LambdaQueryWrapper<SysDepart> query = new LambdaQueryWrapper<SysDepart>();
		query.eq(SysDepart::getParentId,id);
		List<SysDepart> departList = this.list(query);
		if(departList != null && departList.size() > 0) {
			for(SysDepart depart : departList) {
				idList.add(depart.getId());
				this.checkChildrenExists(depart.getId(), idList);
			}
		}
	}

	@Override
	public List<SysDepart> queryUserDeparts(String userId) {
		return baseMapper.queryUserDeparts(userId);
	}

    @Override
    public List<SysDepart> queryUserDepartsByTenantId(String userId, String tenantId) {
        return baseMapper.queryUserDepartsByTenantId(userId, tenantId);
    }

	/**
	 * 根据部门id 获取上级部门
	 *
	 * @param sysCode
	 * @return
	 */
	@Override
	public List<SysDepart> queryParents(String sysCode) {
		List<SysDepart> departs = new ArrayList<>();
		recursiveParents(departs, sysCode);
		return departs;
	}

	private void recursiveParents(List<SysDepart> departs, String sysCode){
		QueryWrapper<SysDepart> where = new QueryWrapper<>();
		where.eq("org_code", sysCode);
		SysDepart depart = baseMapper.selectOne(where);
		departs.add(depart);
		if(StringUtils.isNotEmpty(depart.getParentId())){
			recursiveParents(departs, depart.getParentId());
		}
	}

	@Override
	public List<SysDepart> queryDepartsByUsername(String username) {
		return baseMapper.queryDepartsByUsername(username);
	}

}
