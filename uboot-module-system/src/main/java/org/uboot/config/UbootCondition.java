package org.uboot.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import static org.uboot.common.constant.DataBaseConstant.APP_TENANT_MODEL;

/**
 * @author: LiYang
 * @Email: lyflyyvip@163.com
 * @create: 2020-02-03 14:41
 * @Description:
 **/
public class UbootCondition {

    public class CommonCondition implements Condition{

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return !APP_TENANT_MODEL.equalsIgnoreCase(context.getEnvironment().getProperty("app.tenant.model"));
        }
    }

    public class TenantCondition implements Condition{

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return !APP_TENANT_MODEL.equalsIgnoreCase(context.getEnvironment().getProperty("app.tenant.model"));
        }
    }

}
