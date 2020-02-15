package org.uboot.config.mybatis.permission;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.uboot.common.handler.YamlPropertySourceFactory;

import java.util.Arrays;
import java.util.List;

/**
 * @author: LiYang
 * @Email: lyflyyvip@163.com
 * @create: 2020-02-13 21:43
 * @Description:
 **/
@Data
@Configuration
@PropertySource(value = {"classpath:config/permission-properties.yml"}, factory = YamlPropertySourceFactory.class)
@ConfigurationProperties("permission")
public class PermissionProperties {

    /**
     * 符合条件的表，也就是包含user_id的表,从配置文件中读取
     */
    List<String> tables;

    List<String> departExclusives;

}
