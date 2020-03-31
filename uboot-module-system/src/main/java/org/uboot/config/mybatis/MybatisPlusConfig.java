package org.uboot.config.mybatis;

import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.core.parser.ISqlParser;
import com.baomidou.mybatisplus.core.parser.SqlParserHelper;
import com.baomidou.mybatisplus.extension.injector.LogicSqlInjector;
import com.baomidou.mybatisplus.extension.plugins.tenant.TenantHandler;
import com.baomidou.mybatisplus.extension.plugins.tenant.TenantSqlParser;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.type.EnumTypeHandler;
import org.apache.shiro.SecurityUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PerformanceInterceptor;
import org.springframework.context.annotation.Profile;
import org.uboot.common.constant.TenantConstant;
import org.uboot.common.system.vo.LoginUser;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 单数据源配置（jeecg.datasource.open = false时生效）
 * @Author zhoujf
 *
 */
@Configuration
@MapperScan(value={"org.uboot.modules.**.mapper*"})
public class MybatisPlusConfig {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Resource
    TenantProperties tenantProperties;

    /**
     * mybatis-plus SQL执行效率插件 dev test 环境开启
     */
//    @Bean
//    @Profile({"dev", "test"})
//    public PerformanceInterceptor performanceInterceptor() {
//        PerformanceInterceptor performanceInterceptor = new PerformanceInterceptor();
//        performanceInterceptor.setMaxTime(100);
//        performanceInterceptor.setWriteInLog(true);
//        performanceInterceptor.setFormat(true);
//        return performanceInterceptor;
//    }

    /**
     * mybatis-plus分页插件<br>
     * 文档：http://mp.baomidou.com<br>
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        //3.0 不需要了
//        paginationInterceptor.setLocalPage(true);// 开启 PageHelper 的支持
        /*soldiersPeriod
         * 【多租户】 SQL 解析处理拦截器<br>
         */
        List<ISqlParser> sqlParserList = new ArrayList<ISqlParser>();
        TenantSqlParser tenantSqlParser = new TenantSqlParser();
        tenantSqlParser.setTenantHandler(new TenantHandler() {
            @Override
            public Expression getTenantId() {
                // 从 MilitaryContext 中取实例id
                LoginUser sysUser = getLoginUser();
                LOGGER.debug("login user is -:{}", sysUser);
                return new StringValue((sysUser == null || sysUser.getTenantId() == null) ? "-" : sysUser.getTenantId());
            }

            @Override
            public String getTenantIdColumn() {
                return TenantConstant.TENTANT_DB_ATTR;
            }

            @Override
            public boolean doTableFilter(String tableName) {
                // 这里可以判断是否过滤表
                boolean hasExclusive = tenantProperties.getTableExclusives().contains(tableName);
                LOGGER.debug("tenant matching exclusive table tableName-:{}, hasExclusive-:{}", tableName, hasExclusive);
                if (hasExclusive) {
                    return true;
                }
                return false;
            }
        });


        sqlParserList.add(tenantSqlParser);
        paginationInterceptor.setSqlParserList(sqlParserList);

        paginationInterceptor.setSqlParserFilter(metaObject -> {
            MappedStatement ms = SqlParserHelper.getMappedStatement(metaObject);
            // 过滤自定义查询此时无租户信息约束
            boolean hasExclusive = tenantProperties.getSelectExclusives().contains(ms.getId());
            if (hasExclusive) {
                return true;
            }
            return false;
        });

        return paginationInterceptor;
    }

    /**
     * 注入sql注入器
     */
    @Bean
    public ISqlInjector sqlInjector() {
        return new LogicSqlInjector();
    }

    @Bean
    public ConfigurationCustomizer configurationCustomizer() {
        return new MybatisPlusCustomizers();
    }

    /**
     * 自定义配置
     */
    class MybatisPlusCustomizers implements ConfigurationCustomizer {

        @Override
        public void customize(MybatisConfiguration configuration) {
            configuration.setDefaultEnumTypeHandler(EnumTypeHandler.class);
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
