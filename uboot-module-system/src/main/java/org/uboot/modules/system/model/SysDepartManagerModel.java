package org.uboot.modules.system.model;

import lombok.Data;
import org.uboot.modules.system.entity.SysUser;

import java.util.List;

/**
 * @author: LiYang
 * @Email: lyflyyvip@163.com
 * @create: 2020-02-17 23:33
 * @Description:
 **/
@Data
public class SysDepartManagerModel extends SysUser {

    /**
     * 管理员idlist
     */
    private List<String> managerIds;



}
