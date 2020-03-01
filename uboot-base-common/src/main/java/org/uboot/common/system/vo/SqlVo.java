package org.uboot.common.system.vo;


import lombok.Data;

/**
 * @author: LiYang
 * @Email: lyflyyvip@163.com
 * @create: 2020-03-01 01:39
 * @Description:
 **/
@Data
public class SqlVo {

    public SqlVo(String sql){
        this.sql = sql;
    }

    private String sql;

}

