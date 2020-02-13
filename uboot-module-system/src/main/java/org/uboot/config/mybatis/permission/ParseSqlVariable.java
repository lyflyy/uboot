package org.uboot.config.mybatis.permission;

import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author: LiYang
 * @Email: lyflyyvip@163.com
 * @create: 2020-02-13 01:37
 * @Description:
 **/
public abstract class ParseSqlVariable {

    /**
     * 符合条件的表，也就是包含user_id的表,从配置文件中读取
     */
    final List<String> tables = Arrays.asList("sys_user", "sys_user_depart", "sys_depart", "wm_soldier_info");

    /**
     * 三张系统表，固定表
     */
    final List<String> fromTables = Arrays.asList("sys_user", "sys_user_depart", "sys_depart");


    /**
     * 要设置的被join的表的新的alias
     */
    public static String ORIGIN_TABLE_ALIAS = "sys_user_alias_origin";

    static final Logger logger = LoggerFactory.getLogger(ParseSql.class);

    /**
     * sql的主要查询的主体
     */
    protected PlainSelect selectBody = null;

    /**
     * sql处理处理之前被
     * 符合条件的表的alias名称
     * 可能为空
     */
    protected String oldAliasName = null;

    /**
     * sql处理处理之前被
     * 符合条件的表的alias对象
     * 可能为空
     */
    protected Alias oldAlias = null;

    /**
     * 给符合条件的表设置的alias
     */
    protected Alias newAlias = null;

    /**
     * sql解析 jsqlparser操作主体
     */
    protected Select select = null;

    /**
     * 主表，被left join 的表
     * 都通过get set去调用
     */
    protected FromItem fromItem = null;

    /**
     * 被操作的主表
     */
    protected Table fromTable = null;

    /**
     * 该sql所操作的所有的表
     */
    protected List<Table> tableList = new ArrayList<>();

    /**
     * 该sql所关联的所有表，包括子查询表
     */
    protected List<Join> joins;
}
