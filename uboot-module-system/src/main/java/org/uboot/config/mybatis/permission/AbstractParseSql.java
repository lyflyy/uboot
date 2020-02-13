package org.uboot.config.mybatis.permission;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.*;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import static org.uboot.config.mybatis.permission.ParseSqlUtil.*;

/**
 * @author: LiYang
 * @Email: lyflyyvip@163.com
 * @create: 2020-02-13 00:29
 * @Description:
 **/
public abstract class AbstractParseSql extends ParseSqlVariable {

    /**
     * 设置主表
     * 从主表中获取主表 table
     * 获取alias以及alias name 可能为空
     * @param item
     * @return
     */
    public FromItem setFromItem(FromItem item, String newAliasName){
        // 如果主表为三张系统表的话，主表的alias需要修改为三张系统表的alias
        ORIGIN_TABLE_ALIAS = newAliasName;
        newAlias = new Alias(newAliasName);
        fromItem = item;
        //之前主表的alias
        if(fromItem.getAlias() != null){
            oldAlias = fromItem.getAlias();
            oldAliasName = fromItem.getAlias().getName();
        }
        // 为主表设置新的alias
        fromItem.setAlias(newAlias);
        Table t = (Table) item;
        // 被操作的主表
        fromTable = t;
        return fromItem;
    }


    /**
     * 初始化
     * @param sql
     * @throws JSQLParserException
     */
    public void init(String sql) throws JSQLParserException {
        tableList = new Vector<>();
        select = (Select) CCJSqlParserUtil.parse(sql);
        selectBody = (PlainSelect) select.getSelectBody();
        tableList.add((Table) selectBody.getFromItem());
        joins = selectBody.getJoins();
        if(joins != null){
            for (Join join : joins) {
                tableList.add((Table) join.getRightItem());
            }
        }
    }

    /**
     * 只处理带有alias的表的order by
     */
    protected void handleOrderByAlias(){
        List<OrderByElement> orderByElements = selectBody.getOrderByElements();
        if(orderByElements != null)
            for (OrderByElement orderByElement : orderByElements) {
                handleColumnList(orderByElement.getExpression());
            }
    }

    /**
     * 跟order by 一样的路子
     */
    protected void handleGroupByAlias() {
        List<Expression> list = selectBody.getGroupByColumnReferences();
        if(list != null && list.size() > 0)
        for (Expression expression : list) {
            handleConditionAlias(oldAliasName, expression, fromTable);
        }
    }

    /**
     * 处理group by 和 order by的字段的alias
     * @param expression
     */
    private void handleColumnList(Expression expression){
        Column column = (Column) expression;
        Table orderTable = column.getTable();
        if(orderTable != null && (StringUtils.isBlank(orderTable.getName()) || orderTable.getName().equals(oldAliasName))){
            orderTable.setName(newAlias.getName());
        }
    }

    /**
     * having 与where 一样的套路
     * 多个having条件的话，除了最后一个条件，剩下的都会出现在左expression，右只会剩下一个，递归处理左侧的条件+右1
     */
    protected void handleHavingAlias(Table fromtable){
        Expression having = selectBody.getHaving();
        // 多个where条件的话，除了最后一个条件，剩下的都会出现在左expression，右只会剩下一个，递归处理左侧的条件+右1
        handleLeftExpression(having, oldAliasName, fromtable);
    }


    /**
     * 符合规则并不属于三张系统表
     * 直接将三张系统关联表left join到原sql中
     * 第一张表就设置为sys_user，所以alias为sys_user_alias_origin，跟sys_user相关联的表的原表alias
     * @param item
     * @param skip
     */
    void handledoNotBelongSystem(FromItem item, int skip){
        setFromItem(item, ORIGIN_TABLE_ALIAS);
        Join user = generJoin("sys_user", "sys_user_alias_su", "sys_user_alias_su.id", "sys_user_alias_origin.sys_user_id");
        Join uerDepart = generJoin("sys_user_depart", "sys_depart_alias_sud", "sys_depart_alias_sud.user_id", "sys_user_alias_su.id");
        Join depart = generJoin("sys_depart", "sys_depart_alias_sd", "sys_depart_alias_sd.id", "sys_depart_alias_sud.dep_id");
        List<Join> newJoins = new ArrayList<>();
        newJoins.addAll(Arrays.asList(user, uerDepart, depart));
        List<Join> oldJoins = selectBody.getJoins();
        if(oldJoins != null && oldJoins.size() > 0) {
            handleOldJoinAlias(oldAliasName, oldJoins, fromTable);
            newJoins = handleJoins(oldJoins, newJoins, skip);
        }
        selectBody.setJoins(newJoins);
    }


    /**
     * 属于三张系统表
     * 判断是控制权限的表中是否是三张系统表
     * 判断是属于哪一张表，根据某一张表 处理left join的规则
     * 在拼接sql的时候要用到这张表的alias
     * 拼接的时候三张表不一样，拼接的顺序也不一样
     */
    void handleBelongSystem(String joinTableName, FromItem item, int skip){
        Join join1 = null;
        Join join2 = null;
        List<Join> newJoins = new ArrayList<>();
        switch (joinTableName){
            case "sys_user":
                // origin 要修改成sys_user 的alias
                setFromItem(item, "sys_user_alias_su");
                join1 = generJoin("sys_user_depart", "sys_depart_alias_sud", "sys_depart_alias_sud.user_id", "sys_user_alias_su.id");
                join2 = generJoin("sys_depart", "sys_depart_alias_sd", "sys_depart_alias_sd.id", "sys_depart_alias_sud.dep_id");
                newJoins.addAll(Arrays.asList(join1, join2));
                break;
            case "sys_user_depart":
                setFromItem(item, "sys_depart_alias_sud");
                join1 = generJoin("sys_depart", "sys_depart_alias_sd", "sys_depart_alias_sd.id", "sys_depart_alias_sud.dep_id");
                newJoins.addAll(Arrays.asList(join1));
                break;
            case "sys_depart":
                setFromItem(item, "sys_depart_alias_sd");
                break;
        }
        List<Join> oldJoins = selectBody.getJoins();
        if(oldJoins != null && oldJoins.size() > 0) {
            handleOldJoinAlias(oldAliasName, oldJoins, fromTable);
            newJoins = handleJoins(oldJoins, newJoins, skip);
        }
        selectBody.setJoins(newJoins);
    }


    /**
     * 处理where 中 左右链接的alias
     * 并且拼装 最后的权限控制sql
     * @throws JSQLParserException
     * @param fromTable
     */
    protected void handleWhereAlias(Table fromTable) throws JSQLParserException {
        // 处理where 中 左右链接的alias
        Expression where = selectBody.getWhere();
        // 多个where条件的话，除了最后一个条件，剩下的都会出现在左expression，右只会剩下一个，递归处理左侧的条件+右1
        handleLeftExpression(where, oldAliasName, fromTable);
        Expression departWhere = CCJSqlParserUtil.parseCondExpression("sys_depart_alias_sd.org_code like sys_user_alias_su.org_code");
        if(where == null){
            selectBody.setWhere(departWhere);
        }else{
            AndExpression and = new AndExpression(selectBody.getWhere(), departWhere);
            selectBody.setWhere(and);
        }
    }

    /**
     * 处理select 字段 中 的alias
     */
    void handleSelectAlias() {
        List<SelectItem> selectItems = selectBody.getSelectItems();
        for (SelectItem selectItem : selectItems) {
            if(selectItem instanceof AllTableColumns){
                AllTableColumns allColumns = (AllTableColumns) selectItem;
                Table t = allColumns.getTable();
                if(t != null && t.getName().equals(oldAliasName)){
                    t.setName(ORIGIN_TABLE_ALIAS);
                }
            }
            if(selectItem instanceof SelectExpressionItem){
                SelectExpressionItem selectExpressionItem = (SelectExpressionItem) selectItem;
                if (selectExpressionItem.getExpression() instanceof Column) {
                    Column column = (Column) selectExpressionItem.getExpression();
                    Table table = column.getTable();
                    if(table == null){
                        table.setName(ORIGIN_TABLE_ALIAS);
                    }
                    if(table != null && StringUtils.isBlank(table.getName())){
                        table.setName(ORIGIN_TABLE_ALIAS);
                    }
                }
                handleConditionAlias(oldAliasName, selectExpressionItem.getExpression(), fromTable);
            }

        }
    }


}
