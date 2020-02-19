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
import java.util.stream.IntStream;


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
     */
    public static void handleLeftExpression(Expression where, ParseSqlVo parseSqlVo){
        //BinaryExpression
        if(where != null && (
                where.getClass().getSimpleName().equals("AndExpression") ||
                where.getClass().getSimpleName().equals("EqualsTo")
            )){
            // 多and条件，左侧为多and 右侧为一个条件
            BinaryExpression whereOperator = (BinaryExpression) where;
            handleConditionAlias(parseSqlVo, whereOperator.getRightExpression());
            Expression leftExpression = whereOperator.getLeftExpression();
            if(leftExpression != null && leftExpression.getClass().getSimpleName().equals("AndExpression")){
                handleLeftExpression(leftExpression, parseSqlVo);
            }else{
                handleConditionAlias(parseSqlVo, leftExpression);
            }
        }
    }

    private static void checkFucntionExpression(ParseSqlVo parseSqlVo, Expression expression){
        if(expression.getClass().getSimpleName().equals("Function")){
            Function f = (Function) expression;
            ExpressionList expressionList = f.getParameters();
            for (Expression expressionListExpression : expressionList.getExpressions()) {
                handleConditionAlias(parseSqlVo, expressionListExpression);
            }
        }
    }


    /**
     * 处理条件中左右关联条件的alias
     * 如果左边是个表不是条件 例如 a.id = 3 左边就是表，则替换a为新的alias
     * 右侧同理
     */
    public static void handleConditionAlias(ParseSqlVo parseSqlVo, Expression expression){

        if(expression == null) return;
        // 处理原表中的alias

        if (expression instanceof Column) {
            Column left = (Column) expression;
            handleTableAlias(left.getTable(), parseSqlVo);
        }

        if (expression instanceof Function) {
            checkFucntionExpression(parseSqlVo, expression);
        }

        if (expression instanceof BinaryExpression) {
            BinaryExpression minorThan = (BinaryExpression) expression;
            // 检查是否是mysql的函数
            checkFucntionExpression(parseSqlVo, minorThan);
            try{
                checkFucntionExpression(parseSqlVo, minorThan.getLeftExpression());
                handleLeftExpression(minorThan.getLeftExpression(), parseSqlVo);
                Column left = (Column) minorThan.getLeftExpression();
                if(left != null){
                    handleTableAlias(left.getTable(), parseSqlVo);
                }
            }catch (ClassCastException e){
                logger.info("sql parse MinorThan left is not expression :{}", minorThan.getLeftExpression());
            }
            try{
                checkFucntionExpression(parseSqlVo, minorThan.getRightExpression());
                handleLeftExpression(minorThan.getRightExpression(), parseSqlVo);
                Column right = (Column) minorThan.getRightExpression();
                if(right != null){
                    handleTableAlias(right.getTable(), parseSqlVo);
                }
            }catch (ClassCastException e){
                logger.info("sql parse MinorThan right is not expression :{}", minorThan.getLeftExpression());
            }
        }
        if(expression instanceof IsNullExpression){
            IsNullExpression isNullExpression = (IsNullExpression) expression;
            Column left = (Column) isNullExpression.getLeftExpression();
            if(left != null){
                handleTableAlias(left.getTable(), parseSqlVo);
            }
        }
        if(expression instanceof Between){
            Between between = (Between) expression;
            Column left = (Column) between.getLeftExpression();
            if(left != null){
                handleTableAlias(left.getTable(), parseSqlVo);
            }
        }
    }

    /**
     * 处理table的alias
     * @param table
     */
    private static void handleTableAlias(Table table, ParseSqlVo parseSqlVo) {
        String oldAlias = parseSqlVo.getOldAliasName();
        Table fromTable = parseSqlVo.getFromTable();
        String originTableAlias = parseSqlVo.getOriginTableAlias();
        if(table == null) return;
        String tmpStr[] = table.getFullyQualifiedName().split("\\.");
        // length = 3 database.table.column - length = 2 table/alias.column - length = 1 column
        // length = 3 newAlias.column - length = 2 newAlias.column - length = 1 newAlias.column
        String alias = tmpStr.length == 3 ? tmpStr[1] : tmpStr.length == 2 ? tmpStr[0] : tmpStr[0];
        if(alias.equals(oldAlias) || alias.equals(fromTable.getName()) || StringUtils.isBlank(alias)
        || alias.equals(parseSqlVo.getFromTable().getName())){
            table.setAlias(new Alias(originTableAlias));
        }
    }

    /**
     * 处理原 sql join 关联时的alias逻辑
     */
    public static void handleOldJoinAlias(ParseSqlVo parseSqlVo, List<Join> oldJoins){
        for (Join oldJoin : oldJoins) {
            handleConditionAlias(parseSqlVo, oldJoin.getOnExpression());
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



}
