package org.uboot.config.mybatis;


import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.uboot.common.system.vo.LoginUser;
import org.uboot.config.mybatis.permission.ParseSql;
import org.uboot.config.mybatis.permission.PermissionProperties;
import org.uboot.config.mybatis.permission.annotation.DepartPermission;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.List;
import java.util.Properties;


/**
 * 部门数据权限控制
 */
@Slf4j
@Component
@Intercepts({ @Signature(method = "prepare", type = StatementHandler.class, args = { Connection.class, Integer.class }) })
public class DepartPermissionInterceptor implements Interceptor {

    @Resource
    PermissionProperties permissionProperties;

    @Autowired
    private ParseSql parseSql;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler handler = (StatementHandler)invocation.getTarget();
        //由于mappedStatement中有需要的方法id,但却是protected的，所以要通过反射获取
        MetaObject statementHandler = SystemMetaObject.forObject(handler);
        MappedStatement mappedStatement = (MappedStatement) statementHandler.getValue("delegate.mappedStatement");

        LoginUser sysUser = getLoginUser();
        if(sysUser == null){
            return invocation.proceed();
        }else if(sysUser.getUsername().equals("admin")){
            return invocation.proceed();
        }

        //获得方法类型 (如select,update)
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        if ("SELECT".equalsIgnoreCase(sqlCommandType.toString())) {
            String mapperId = mappedStatement.getId();
            if(determineWhetherToPerform(mapperId)){
                //获取sql
                BoundSql boundSql = handler.getBoundSql();
                String sql = boundSql.getSql();
                //将增强后的sql放回
                statementHandler.setValue("delegate.boundSql.sql",parseSql.handle(sql));
            }
        }
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }

    /**
     * 判断是否需要处理sql
     * @param mapperId
     * @return
     * @throws ClassNotFoundException
     */
    private Boolean determineWhetherToPerform(String mapperId) throws ClassNotFoundException {
        String mapperClassName = mapperId.substring(0, mapperId.lastIndexOf("."));
        String mapperMethodName = mapperId.substring(mapperId.lastIndexOf(".") + 1);
        Class<?> mapperClass = Class.forName(mapperClassName);
        Method[] methods = mapperClass.getMethods();
        Method method = null;
        for (Method m : methods) {
            if(m.getName().equals(mapperMethodName)){
                method = m;
                break;
            }
        }
        DepartPermission departPermission = AnnotationUtils.findAnnotation(mapperClass, DepartPermission.class);
        List<String> departExclusives = permissionProperties.getDepartExclusives();
        if(departExclusives != null && departExclusives.contains(mapperId)){
            // 配置文件排除该sql，不需要处理了
            return false;
        }else{
            // 需要处理
            if(departPermission != null){
                // 类上标有注解
                return true;
            }else{
                // 类上没有注解，查看具体的方法是是否有注解
                departPermission = method.getAnnotation(DepartPermission.class);
                if(departPermission != null){
                    // 方法上有该注解
                    return true;
                }else{
                    return false;
                }
            }
        }
    }



    //update-begin--Author:scott  Date:20191213 for：关于使用Quzrtz 开启线程任务， #465
    private LoginUser getLoginUser() {
        LoginUser sysUser = null;
        try {
            sysUser = SecurityUtils.getSubject().getPrincipal() != null ? (LoginUser) SecurityUtils.getSubject().getPrincipal() : null;
        } catch (Exception e) {
            //e.printStackTrace();
            sysUser = null;
        }
        return sysUser;
    }
    //update-end--Author:scott  Date:20191213 for：关于使用Quzrtz 开启线程任务， #465


}
