package org.uboot.config.mybatis.permission;

import lombok.Data;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;

import java.util.List;
import java.util.Vector;

/**
 * @author: LiYang
 * @Email: lyflyyvip@163.com
 * @create: 2020-02-14 15:39
 * @Description:
 **/
@Data
public class ParseSqlVo {

    /**
     * 当前登陆用户的部门code
     */
    protected String currentOrgCode = "";

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
    protected Vector<Table> tableList = new Vector<>();

    /**
     * 该sql所关联的所有表，包括子查询表
     */
    protected List<Join> joins;

    /**
     * 要设置的被join的表的新的alias
     */
    public String originTableAlias;


}
