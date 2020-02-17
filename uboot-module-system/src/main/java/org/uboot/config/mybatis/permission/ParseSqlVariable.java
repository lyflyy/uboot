package org.uboot.config.mybatis.permission;

import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

/**
 * @author: LiYang
 * @Email: lyflyyvip@163.com
 * @create: 2020-02-13 01:37
 * @Description:
 **/
public abstract class ParseSqlVariable {

    /**
     * 系统表，固定表
     */
    final String fromTables = "sys_user";

    static final Logger logger = LoggerFactory.getLogger(ParseSql.class);

}
