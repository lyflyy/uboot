package org.uboot.config.mybatis.permission;

import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.operators.relational.Between;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.IsNullExpression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.Join;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.uboot.config.mybatis.permission.ParseSql.ORIGIN_TABLE_ALIAS;

/**
 * @author: LiYang
 * @Email: lyflyyvip@163.com
 * @create: 2020-02-02 18:57
 * @Description:
 **/
public class ParseSqlUtil {

    static Logger logger = LoggerFactory.getLogger(ParseSqlUtil.class);

    /**
     * 多个where条件的话，除了最后一个条件，剩下的都会出现在左expression，右只会剩下一个，递归处理左侧的条件+右1
     * @param where
     * @param oldAlias
     * @param fromtable
     */
    public static void handleLeftExpression(Expression where, String oldAlias, Table fromtable){
        if(where != null && where.getClass().getSimpleName().equals("AndExpression")){
            // 多and条件，左侧为多and 右侧为一个条件
            BinaryExpression whereOperator = (BinaryExpression) where;
            handleConditionAlias(oldAlias, whereOperator.getRightExpression(), fromtable);
            Expression leftExpression = whereOperator.getLeftExpression();
            if(leftExpression != null && leftExpression.getClass().getSimpleName().equals("AndExpression")){
                handleLeftExpression(leftExpression, oldAlias, fromtable);
            }else{
                handleConditionAlias(oldAlias, leftExpression, fromtable);
            }
        }
    }

    private static void checkFucntionExpression(String oldAlias, Expression expression, Table fromTable){
        if(expression.getClass().getSimpleName().equals("Function")){
            Function f = (Function) expression;
            ExpressionList expressionList = f.getParameters();
            for (Expression expressionListExpression : expressionList.getExpressions()) {
                handleConditionAlias(oldAlias, expressionListExpression, fromTable);
            }
        }
    }


    /**
     * 处理条件中左右关联条件的alias
     * 如果左边是个表不是条件 例如 a.id = 3 左边就是表，则替换a为新的alias
     * 右侧同理
     * @param oldAlias
     * @param expression
     */
    public static void handleConditionAlias(String oldAlias, Expression expression, Table fromTable){
        if(expression == null) return;
        // 处理原表中的alias

        if (expression instanceof Column) {
            Column left = (Column) expression;
            handleTableAlias(left.getTable(), oldAlias, fromTable);
        }

        if (expression instanceof Function) {
            checkFucntionExpression(oldAlias, expression, fromTable);
        }

        if (expression instanceof BinaryExpression) {
            BinaryExpression minorThan = (BinaryExpression) expression;
            // 检查是否是mysql的函数
            checkFucntionExpression(oldAlias, minorThan, fromTable);
            try{
                checkFucntionExpression(oldAlias, minorThan.getLeftExpression(), fromTable);
                Column left = (Column) minorThan.getLeftExpression();
                if(left != null){
                    handleTableAlias(left.getTable(), oldAlias, fromTable);
                }
            }catch (ClassCastException e){
                logger.info("sql parse MinorThan left is not expression :{}", minorThan.getLeftExpression());
            }
            try{
                checkFucntionExpression(oldAlias, minorThan.getRightExpression(), fromTable);
                Column right = (Column) minorThan.getRightExpression();
                if(right != null){
                    handleTableAlias(right.getTable(), oldAlias, fromTable);
                }
            }catch (ClassCastException e){
                logger.info("sql parse MinorThan right is not expression :{}", minorThan.getLeftExpression());
            }
        }
        if(expression instanceof IsNullExpression){
            IsNullExpression isNullExpression = (IsNullExpression) expression;
            Column left = (Column) isNullExpression.getLeftExpression();
            if(left != null){
                handleTableAlias(left.getTable(), oldAlias, fromTable);
            }
        }
        if(expression instanceof Between){
            Between between = (Between) expression;
            Column left = (Column) between.getLeftExpression();
            if(left != null){
                handleTableAlias(left.getTable(), oldAlias, fromTable);
            }
        }
    }

    /**
     * 处理table的alias
     * @param table
     * @param oldAlias
     * @param fromTable
     */
    private static void handleTableAlias(Table table, String oldAlias, Table fromTable) {
        if(table == null) return;
        String tmpStr[] = table.getFullyQualifiedName().split("\\.");
        // length = 3 database.table.column - length = 2 table/alias.column - length = 1 column
        // length = 3 newAlias.column - length = 2 newAlias.column - length = 1 newAlias.column
        String alias = tmpStr.length == 3 ? tmpStr[1] : tmpStr.length == 2 ? tmpStr[0] : tmpStr[0];
        if(alias.equals(oldAlias) || alias.equals(fromTable.getName()) || StringUtils.isBlank(alias)){
            table.setAlias(new Alias(ORIGIN_TABLE_ALIAS));
        }
    }

    /**
     * 处理原 sql join 关联时的alias逻辑
     * @param oldAlias
     * @param oldJoins
     * @param fromTable
     */
    public static void handleOldJoinAlias(String oldAlias, List<Join> oldJoins, Table fromTable){
        for (Join oldJoin : oldJoins) {
            handleConditionAlias(oldAlias, oldJoin.getOnExpression(), fromTable);
        }
    }

    /**
     * 生成join表以及join条件
     * @param tableName
     * @param alias
     * @param left
     * @param right
     * @return
     */
    public static Join generJoin(String tableName, String alias, String left, String right){
        Join join = new Join();
        join.setLeft(true);
        Table table = new Table(tableName);
        table.setAlias(new Alias(alias, true));
        join.setRightItem(table);
        join.setOnExpression(andExpression(left, right));
        return join;
    }

    /**
     * 获得join on条件表达式
     * @param left
     * @param right
     * @return
     */
    protected static BinaryExpression andExpression(String left, String right) {
        EqualsTo equalsTo = new EqualsTo();
        equalsTo.setLeftExpression(new Column(left));
        equalsTo.setRightExpression(new Column(right));
        return equalsTo;
    }


    /**
     * 两个动态数组，skip为插入索引，从old中的第几个开始插入
     * @param oldList
     * @param newList
     * @param skip
     * @return
     */
    protected static List<Join> handleJoins(List<Join> oldList, List<Join> newList, Integer skip){
        List<Join> list = new ArrayList<>();
        LinkedList oldLinked = new LinkedList(oldList);
        LinkedList newLinkedList = new LinkedList(newList);
        IntStream.range(0, skip == null ? 0 : skip).forEach(e -> {
            list.add((Join) oldLinked.pop());
        });
        list.addAll(newLinkedList);
        list.addAll(oldLinked);
        return list;
    }

    /**
     * 判断两个list是否包含相同元素
     * @param l1
     * @param l2
     * @return
     */
    protected static Boolean judgeIncludeCollections(List<Table> l1, List<String> l2){
        Set<String> t1 = new HashSet<>(l1.stream()
                .filter(e -> e != null && StringUtils.isNoneBlank(e.getName()))
                .map(e -> e.getName()).collect(Collectors.toList()));
        Set<String> t2 = new HashSet<>(l2);
        t1.addAll(t2);
        return t1.size() != l1.size() + l2.size();
    }



}