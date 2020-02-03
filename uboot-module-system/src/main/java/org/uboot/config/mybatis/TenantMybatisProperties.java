package org.uboot.config.mybatis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.tenant")
public class TenantMybatisProperties {

    List<String> tableExclusives;

    List<String> selectExclusives;
}
