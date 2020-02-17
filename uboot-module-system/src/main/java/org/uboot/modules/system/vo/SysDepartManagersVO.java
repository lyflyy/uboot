package org.uboot.modules.system.vo;

import lombok.Data;
import org.uboot.modules.system.entity.SysDepart;

import java.io.Serializable;
import java.util.List;


/**
 * 部门添加时设置管理员
 */
@Data
public class SysDepartManagersVO extends SysDepart implements Serializable{
	private static final long serialVersionUID = 1L;

	/**对应的用户id集合*/
	private List<String> userId;

}
