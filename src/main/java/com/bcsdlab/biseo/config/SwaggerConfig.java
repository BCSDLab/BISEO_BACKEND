package com.bcsdlab.biseo.config;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
@EnableWebMvc
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.OAS_30)
            .apiInfo(getApiInfo())                      // Api Info
//            .consumes(getConsumeContentTypes())         // Request Content-type
//            .produces(getProduceContentTypes())         // Response Content-type
            .select()                                   // ApiSelectBuilder 생성
            .apis(RequestHandlerSelectors.basePackage("com.bcsdlab.biseo.controller"))  // api 스펙이 작성될 패키지
            .paths(PathSelectors.any())                 // path 조건에 해당하는 api만 불러옴
            .build()
            .securitySchemes(Arrays.asList(getApiKey()));
    }

    private Set<String> getConsumeContentTypes() {
        Set<String> consumes = new HashSet<>();
        consumes.add("application/json;charset=UTF-8");
        consumes.add("application/x-www-form-urlencoded");
        consumes.add("multipart/form-data");
        return consumes;
    }

    private Set<String> getProduceContentTypes() {
        Set<String> produces = new HashSet<>();
        produces.add("application/json;charset=UTF-8");
        return produces;
    }

    private ApiInfo getApiInfo() {
        return new ApiInfoBuilder()
            .title("Biseo API")
            .description("BCSDLab Biseo API Docs")
            .version("1.0")
            .build();
    }

    private ApiKey getApiKey() {
        return new ApiKey("Authorization", "Authorization", "header");
    }
}
