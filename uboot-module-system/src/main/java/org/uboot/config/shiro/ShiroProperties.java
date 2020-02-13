package org.uboot.config.shiro;

import lombok.Data;
import org.apache.shiro.util.AntPathMatcher;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.uboot.common.handler.YamlPropertySourceFactory;

import java.util.List;
import java.util.regex.Pattern;

/**
 * @author: LiYang
 * @Email: lyflyyvip@163.com
 * @create: 2020-02-02 16:58
 * @Description:
 **/
@Data
@Configuration
@PropertySource(value = {"classpath:config/shiro-properties.yml"}, factory = YamlPropertySourceFactory.class)
@ConfigurationProperties("shiro")
public class ShiroProperties {

    private List<String> excludeUrls;

    public Boolean contains(String url){
        for(String pattern: excludeUrls){
            AntPathMatcher antPathMatcher = new AntPathMatcher();
            if(antPathMatcher.match(pattern, url)){
                return true;
            }
        }
        return false;

    }

}
