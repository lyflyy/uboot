package org.uboot.config.mybatis;

import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.uboot.common.system.vo.LoginUser;
import org.uboot.config.mybatis.permission.ParseSql;
import org.uboot.config.mybatis.permission.PermissionProperties;
import org.uboot.config.mybatis.permission.annotation.DepartPermission;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.util.List;
import java.util.Properties;

/**
 * 部门数据权限控制
 * 为解决分页插件在自定义插件前执行，参考https://github.com/pagehelper/Mybatis-PageHelper/blob/master/wikis/zh/Interceptor.md
 */
@Slf4j
@Component
@Intercepts(
        {
            @Signature(type = Executor.class, method = "query", args =
                    {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
            @Signature(type = Executor.class, method = "query", args =
                    {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        }
)
public class DepartPermissionInterceptor implements Interceptor {

    @Resource
    PermissionProperties permissionProperties;

    @Autowired
    private ParseSql parseSql;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        Object parameter = args[1];
        RowBounds rowBounds = (RowBounds) args[2];
        ResultHandler resultHandler = (ResultHandler) args[3];
        Executor executor = (Executor) invocation.getTarget();
        CacheKey cacheKey;
        BoundSql boundSql;
        //由于逻辑关系，只会进入一次
        if(args.length == 4){
            //4 个参数时
            boundSql = ms.getBoundSql(parameter);
            cacheKey = executor.createCacheKey(ms, parameter, rowBounds, boundSql);
        } else {
            //6 个参数时
            cacheKey = (CacheKey) args[4];
            boundSql = (BoundSql) args[5];
        }
        //TODO 自己要进行的各种处理
        //注：下面的方法可以根据自己的逻辑调用多次，在分页插件中，count 和 page 各调用了一次

        LoginUser sysUser = getLoginUser();
        if(sysUser == null){
            return executor.query(ms, parameter, rowBounds, resultHandler, cacheKey, boundSql);
        }else if(sysUser.getUsername().equals("admin")){
            return executor.query(ms, parameter, rowBounds, resultHandler, cacheKey, boundSql);
        }

//        StatementHandler handler = (StatementHandler)invocation.getTarget();
//        //由于mappedStatement中有需要的方法id,但却是protected的，所以要通过反射获取
//        MetaObject statementHandler = SystemMetaObject.forObject(handler);

        //获得方法类型 (如select,update)
        SqlCommandType sqlCommandType = ms.getSqlCommandType();
        if ("SELECT".equalsIgnoreCase(sqlCommandType.toString())) {
            String mapperId = ms.getId();
            if(determineWhetherToPerform(mapperId)){
                //获取sql
                String sql = boundSql.getSql();
                //将增强后的sql放回
                modify(boundSql, "sql", parseSql.handle(sql));
                return executor.query(ms, parameter, rowBounds, resultHandler, cacheKey, boundSql);
            }
        }
        return executor.query(ms, parameter, rowBounds, resultHandler, cacheKey, boundSql);
    }

    private static void modify(Object object, String fieldName, Object newFieldValue){
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            if(!field.isAccessible()) {
                field.setAccessible(true);
            }
            field.set(object, newFieldValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }

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

}
