package org.uboot.modules.system.model;

import lombok.Data;
import org.uboot.modules.system.entity.SysDepart;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 部门表 存储树结构数据的实体类
 * 继承自SysDepartTreeModel
 * 包含管理员信息字段
 * <p>
 *
 * @Author Steve
 * @Since 2019-01-22
 */
@Data
public class SysDepartTreeWithManagerModel extends SysDepartModel implements Serializable{

    private static final long serialVersionUID = 1L;


}
