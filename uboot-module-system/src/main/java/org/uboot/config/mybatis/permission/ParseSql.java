package org.uboot.config.mybatis.permission;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.Join;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Component;
import org.uboot.common.exception.UBootException;
import org.uboot.common.system.vo.LoginUser;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * @author: LiYang
 * @Email: lyflyyvip@163.com
 * @create: 2020-02-12 16:47
 * @Description:
 **/
@Component
public class ParseSql extends AbstractParseSql{

    @Resource
    private PermissionProperties permissionProperties;

    private void initializesObject(){
        ParseSqlVo vo = new ParseSqlVo();
        this.parseSqlVo = vo;
    }

    /**
     * 使用条件
     *  1. 固定mapper 需要在配置文件中声明出来,mapper下所有的查询都会使用
     *  2. 可过滤制定select id
     *
     * 1. fromtable是否符合条件，mapper上增加注解，筛选符合权限操作的mapper id
     * 2. 判断执行方式，
     *      1-fromtable符合条件
     *          1-不属于系统表
     *          2-属于系统表
     *      2-jointable符合条件
     *          1-不属于系统表
     *          2-属于系统表
     *      3-都不符合条件，这个sql不需要处理，但是如果这个sql被添加了注解，则需要抛出异常
     *
     * 如果主表是子查询的话，没办法处理，只能处理剩下的表，因为子查询不能知道查询出来的内容是否包含user_id 能做的上关联，
     * 所以在写sql的时候，主表一定要是配置好的筛选的表
     * @return
     */
    // todo - 非子查询，子查询暂时没处理，如果一个sql中除子查询的sql
    //  都不符合权限查询表规则的话，子查询是没有处理的，这个sql就没办法使用该权限规则
    public String handle(String sql) throws JSQLParserException {

        /**
         * 初始化变量对象
         */
        this.initializesObject();

        /**
         * 为本地main方法可以进行调用，该list需要修改
         */
        List<String> tables;

        if(permissionProperties == null){
            tables = Arrays.asList("sys_user", "sys_user_depart", "sys_depart", "wm_soldier_info");
            parseSqlVo.setCurrentOrgCode("A03");
        }else{
            tables = permissionProperties.getTables();
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

            if(sysUser == null){
                throw new UBootException("无数据权限，此操作需用户登录！");
            }

            parseSqlVo.setCurrentOrgCode(sysUser.getOrgCode());
        }

        /**
         * 必须要先执行init方法
         */
        super.init(sql);

        Table table = (Table) parseSqlVo.getSelectBody().getFromItem();
        /**
         * 从该sql所查询的所有表中过滤是否包含系统表
         */
        if(judgeIncludeCollections(parseSqlVo.getTableList(), fromTables)){
            // 所有表中肯定包含系统表
            // 1. 先检查fromtable是否为系统表
            if(parseSqlVo.getSelectBody().getFromItem() instanceof Table && fromTables.equals(table.getName())){
                // 是系统表sys_user
                handleBelongSystem(table.getName(), parseSqlVo.getSelectBody().getFromItem(), 0);
            }else{
                // 2. 再检查jointable 是否为系统表
                if(parseSqlVo.getJoins() != null && parseSqlVo.getJoins().size() > 0){
                    for (int i = 0; i < parseSqlVo.getJoins().size(); i++) {
                        Join join = parseSqlVo.getJoins().get(i);
                        Table joinTable = (Table) join.getRightItem();
                        // jointable 非子查询并且为系统表
                        if(join.getRightItem() instanceof Table && fromTables.equals(joinTable.getName())){
                            handleBelongSystem(joinTable.getName(), join.getRightItem(), i + 1);
                        }
                    }
                }
            }
        }else{
            // 该sql所查询到的表不包含系统表，判断是否符合条件的表，也就是包含user_id字段的表
            if(parseSqlVo.getSelectBody().getFromItem() instanceof Table && tables.contains(table.getName())){
                // 主表符合条件
                handledoNotBelongSystem(parseSqlVo.getSelectBody().getFromItem(), 0);
            }else{
                // 主表不符合条件，遍历jointables
                if(parseSqlVo.getJoins() != null)
                for (int i = 0; i < parseSqlVo.getJoins().size(); i++) {
                    Join join = parseSqlVo.getJoins().get(i);
                    // jointable 不是子查询
                    Table joinTable = (Table) join.getRightItem();
                    if(join.getRightItem() instanceof Table && tables.contains(joinTable.getName())){
                        handledoNotBelongSystem(join.getRightItem(), i + 1);
                        //有符合条件的表可以作为主表存在了，可以直接跳出循环了
                        break;
                    }else{
                        logger.info("sql joinTable is SubSelect");
                    }
                }
            }
        }
        // 处理select 字段 中 的alias
        handleSelectAlias();
        // 处理where 中 左右链接的alias
        handleWhereAlias();
        // 处理having 中的alias
        handleHavingAlias();
        // 处理order by 中 的alias
        handleOrderByAlias();
        // 处理group by 中 的alias
        handleGroupByAlias();
        // limit 不需要处理
        return parseSqlVo.getSelect().toString();
    }

}
