
package org.uboot.config.condition;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import static org.uboot.common.constant.DataBaseConstant.APP_TENANT_MODEL;

public class CommonCondition implements Condition {

        public CommonCondition(){}

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return !APP_TENANT_MODEL.equalsIgnoreCase(context.getEnvironment().getProperty("app.tenant.model"));
        }
    }
