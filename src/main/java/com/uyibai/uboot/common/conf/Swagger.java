package com.uyibai.uboot.common.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author: LiYang
 * @Email: lyflyyvip@163.com
 * @create: 2020-01-15 14:14
 * @Description:
 **/
@Configuration(value="false")
@EnableSwagger2 //启动swagger注解 启动服务，浏览器输入"http://服务名:8080/swagger-ui.html"
public class Swagger {
	@Bean
	public Docket createRestApi() {
		return new Docket(DocumentationType.SWAGGER_2)
				.apiInfo(apiInfo())
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.uyibai.uboot.controller"))
				.paths(PathSelectors.any())
				.build();
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
				.title("uboot API文档")
				.description("简单优雅的restfun风格，https://github.com/lyflyy/uboot")
				.termsOfServiceUrl("https://github.com/lyflyy/uboot")
				.version("1.0")
				.build();
	}

}
