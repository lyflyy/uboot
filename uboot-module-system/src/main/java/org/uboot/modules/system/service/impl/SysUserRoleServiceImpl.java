package org.uboot.modules.system.service.impl;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.uboot.modules.system.entity.SysRole;
import org.uboot.modules.system.entity.SysUser;
import org.uboot.modules.system.entity.SysUserRole;
import org.uboot.modules.system.mapper.SysUserMapper;
import org.uboot.modules.system.mapper.SysUserRoleMapper;
import org.uboot.modules.system.service.ISysRoleService;
import org.uboot.modules.system.service.ISysUserRoleService;
import org.uboot.modules.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * <p>
 * 用户角色表 服务实现类
 * </p>
 *
 * @Author scott
 * @since 2018-12-21
 */
@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements ISysUserRoleService {

	@Autowired
	private ISysRoleService roleService;

	@Autowired
    private SysUserMapper sysUserMapper;

	/**
	 * 查询所有用户对应的角色信息
	 */
	@Override
	public Map<String,String> queryUserRole() {
		List<SysUserRole> uRoleList = this.list();
		List<SysUser> userList = sysUserMapper.selectList(Wrappers.emptyWrapper());
		List<SysRole> roleList = roleService.list();
		Map<String,String> map = new IdentityHashMap<>();
		String userId = "";
		String roleId = "";
		String roleName = "";
		if(uRoleList != null && uRoleList.size() > 0) {
			for(SysUserRole uRole : uRoleList) {
				roleId = uRole.getRoleId();
				for(SysUser user : userList) {
					userId = user.getId();
					if(uRole.getUserId().equals(userId)) {
						roleName = this.searchByRoleId(roleList,roleId);
						map.put(userId, roleName);
					}
				}
			}
			return map;
		}
		return map;
	}

	/**
	 * queryUserRole调用的方法
	 * @param roleList
	 * @param roleId
	 * @return
	 */
	private String searchByRoleId(List<SysRole> roleList, String roleId) {
		while(true) {
			for(SysRole role : roleList) {
				if(roleId.equals(role.getId())) {
					return role.getRoleName();
				}
			}
		}
	}

}
