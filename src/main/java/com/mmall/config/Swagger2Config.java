package com.mmall.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Copyright (C), 2018, GeforceLee
 *
 * @author: geforce
 * @Date: 2018/4/1 下午12:09
 */
@Configuration
@EnableSwagger2
@EnableWebMvc
public class Swagger2Config {
    @Bean
    public Docket buildDocket(){
        return new Docket(DocumentationType.SWAGGER_2)
                .ignoredParameterTypes(ApiIgnore.class)
                .apiInfo(buildApiInf())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.mmall.controller"))
                .paths(PathSelectors.any())

                .build();
    }

    private ApiInfo buildApiInf(){
        return new ApiInfoBuilder()
                .title("MMall接口")
                .description("接口文档")
                .contact(new Contact("GeforceLee", "www.eget.me", "314004856@qq.com"))
                .build();
    }

}
