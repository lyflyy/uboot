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
     * 变量提取，防止并发问题
     */
    protected ParseSqlVo parseSqlVo;

    /**
     * 设置主表
     * 从主表中获取主表 table
     * 获取alias以及alias name 可能为空
     * @param item
     * @return
     */
    public FromItem setFromItem(FromItem item, String newAliasName){
        // 如果主表为三张系统表的话，主表的alias需要修改为三张系统表的alias
        parseSqlVo.setOriginTableAlias(newAliasName);
        parseSqlVo.setNewAlias(new Alias(newAliasName));
        parseSqlVo.setFromItem(item);
        //之前主表的alias
        if(parseSqlVo.getFromItem().getAlias() != null){
            parseSqlVo.setOldAlias(
                    parseSqlVo.getFromItem().getAlias()
            );
            parseSqlVo.setOldAliasName(
                    parseSqlVo.getFromItem().getAlias().getName()
            );
        }
        // 为主表设置新的alias
        parseSqlVo.getFromItem().setAlias(parseSqlVo.getNewAlias());
        Table t = (Table) item;
        // 被操作的主表
        parseSqlVo.setFromTable(t);
        return parseSqlVo.getFromItem();
    }


    /**
     * 初始化
     * @param sql
     * @throws JSQLParserException
     */
    public void init(String sql) throws JSQLParserException {
        Select select = (Select) CCJSqlParserUtil.parse(sql);
        PlainSelect selectBody = (PlainSelect) select.getSelectBody();

        parseSqlVo.getTableList().clear();
        parseSqlVo.setSelect(select);
        parseSqlVo.setSelectBody(selectBody);

        parseSqlVo.getTableList().add((Table) selectBody.getFromItem());

        List<Join> joins = selectBody.getJoins();
        parseSqlVo.setJoins(selectBody.getJoins());
        if(joins != null){
            for (Join join : joins) {
                parseSqlVo.getTableList().add((Table) join.getRightItem());
            }
        }
    }

    /**
     * 只处理带有alias的表的order by
     */
    protected void handleOrderByAlias(){
        List<OrderByElement> orderByElements = parseSqlVo.getSelectBody().getOrderByElements();
        if(orderByElements != null)
            for (OrderByElement orderByElement : orderByElements) {
                handleColumnList(orderByElement.getExpression());
            }
    }

    /**
     * 跟order by 一样的路子
     */
    protected void handleGroupByAlias() {
        List<Expression> list = parseSqlVo.getSelectBody().getGroupByColumnReferences();
        if(list != null && list.size() > 0)
        for (Expression expression : list) {
            handleConditionAlias(parseSqlVo, expression);
        }
    }

    /**
     * 处理group by 和 order by的字段的alias
     * @param expression
     */
    private void handleColumnList(Expression expression){
        Column column = (Column) expression;
        Table orderTable = column.getTable();
        if(orderTable != null &&
                (StringUtils.isBlank(orderTable.getName()) ||
                        orderTable.getName().equals(parseSqlVo.getOldAliasName()))){
            orderTable.setName(parseSqlVo.getNewAlias().getName());
        }
    }

    /**
     * having 与where 一样的套路
     * 多个having条件的话，除了最后一个条件，剩下的都会出现在左expression，右只会剩下一个，递归处理左侧的条件+右1
     */
    protected void handleHavingAlias(){

        Expression having = parseSqlVo.getSelectBody().getHaving();
        // 多个where条件的话，除了最后一个条件，剩下的都会出现在左expression，右只会剩下一个，递归处理左侧的条件+右1
        handleLeftExpression(having, parseSqlVo);
    }


    /**
     * 符合规则并不属于三张系统表
     * 直接将三张系统关联表left join到原sql中
     * 第一张表就设置为sys_user，所以alias为sys_user_alias_origin，跟sys_user相关联的表的原表alias
     * @param item
     * @param skip
     */
    void handledoNotBelongSystem(FromItem item, int skip){
        setFromItem(item, "sys_user_alias_origin");
        Join user = generJoin("sys_user", "sys_user_alias_su", "sys_user_alias_su.id", "sys_user_alias_origin.user_id");
        List<Join> newJoins = new ArrayList<>();
        newJoins.add(user);
        List<Join> oldJoins = parseSqlVo.getSelectBody().getJoins();
        if(oldJoins != null && oldJoins.size() > 0) {
            handleOldJoinAlias(parseSqlVo, oldJoins);
            newJoins = handleJoins(oldJoins, newJoins, skip);
        }
        parseSqlVo.getSelectBody().setJoins(newJoins);
    }


    /**
     * 属于三张系统表
     * 判断是控制权限的表中是否是三张系统表
     * 判断是属于哪一张表，根据某一张表 处理left join的规则
     * 在拼接sql的时候要用到这张表的alias
     * 拼接的时候三张表不一样，拼接的顺序也不一样
     */
    void handleBelongSystem(String joinTableName, FromItem item, int skip){
        List<Join> newJoins = new ArrayList<>();
        if(joinTableName.equals("sys_user")){
            // origin 要修改成sys_user 的alias
            setFromItem(item, "sys_user_alias_su");
        }else{
            setFromItem(item, "sys_user_alias_origin");
        }
        List<Join> joins = parseSqlVo.getSelectBody().getJoins();
        // 处理关联表的alias
        if(joins != null && joins.size() > 0) {
            handleOldJoinAlias(parseSqlVo, joins);
            newJoins = handleJoins(joins, newJoins, skip);
        }
        parseSqlVo.getSelectBody().setJoins(newJoins);
    }


    /**
     * 处理where 中 左右链接的alias
     * 并且拼装 最后的权限控制sql
     * @throws JSQLParserException
     */
    protected void handleWhereAlias() throws JSQLParserException {
        // 处理where 中 左右链接的alias
        Expression where = parseSqlVo.getSelectBody().getWhere();
        // 多个where条件的话，除了最后一个条件，剩下的都会出现在左expression，右只会剩下一个，递归处理左侧的条件+右1
        handleLeftExpression(where, parseSqlVo);
        Expression departWhere = CCJSqlParserUtil.parseCondExpression("sys_user_alias_su.org_code LIKE '" + parseSqlVo.getCurrentOrgCode() + "%'");
        if(where == null){
            parseSqlVo.getSelectBody().setWhere(departWhere);
        }else{
            AndExpression and = new AndExpression(parseSqlVo.getSelectBody().getWhere(), departWhere);
            parseSqlVo.getSelectBody().setWhere(and);
        }
    }

    /**
     * 处理select 字段 中 的alias
     */
    void handleSelectAlias() {
        List<SelectItem> selectItems = parseSqlVo.getSelectBody().getSelectItems();
        for (SelectItem selectItem : selectItems) {
            if(selectItem instanceof AllTableColumns){
                AllTableColumns allColumns = (AllTableColumns) selectItem;
                Table t = allColumns.getTable();
                if(t != null && (
                        t.getName().equals(parseSqlVo.getOldAliasName())) ||
                        t.getName().equals(parseSqlVo.getFromTable().getName())
                ){
                    t.setName(parseSqlVo.getOriginTableAlias());
                }
            }
            if(selectItem instanceof SelectExpressionItem){
                SelectExpressionItem selectExpressionItem = (SelectExpressionItem) selectItem;
                if (selectExpressionItem.getExpression() instanceof Column) {
                    Column column = (Column) selectExpressionItem.getExpression();
                    Table table = column.getTable();
                    if(table == null){
                        table.setName(parseSqlVo.getOriginTableAlias());
                    }
                    if(table != null && StringUtils.isBlank(table.getName())){
                        table.setName(parseSqlVo.getOriginTableAlias());
                    }
                }
                handleConditionAlias(parseSqlVo, selectExpressionItem.getExpression());
            }

        }
    }

    protected boolean judgeIncludeCollections(Vector<Table> tableList, String fromTables) {
        for (Table table : tableList) {
            if(table.getName().equals(fromTables)){
                return true;
            }
        }
        return false;
    }


}
