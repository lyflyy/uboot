package org.uboot.config.mybatis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.uboot.common.handler.YamlPropertySourceFactory;

import java.util.List;

@Data
@Configuration
@PropertySource(value = {"classpath:config/tenant-properties.yml"}, factory = YamlPropertySourceFactory.class)
@ConfigurationProperties(prefix = "app.tenant")
public class TenantProperties {

    List<String> tableExclusives;

    List<String> selectExclusives;

    List<String> departExclusives;
}
