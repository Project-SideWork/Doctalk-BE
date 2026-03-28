package com.capstone.global.config;

import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        // Define the security scheme
        SecurityScheme apiKey = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");

        // Define the security requirement
        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("Authorization");

        // Add the security scheme to components
        Components components = new Components()
                .addSecuritySchemes("Authorization", apiKey);

        return new OpenAPI()
                .addServersItem(new Server().url("/"))
                .components(components)
                .addSecurityItem(securityRequirement)
                .info(apiInfo());
    }

    @Bean
    public org.springdoc.core.customizers.OpenApiCustomizer prependApiPrefix() {
        return openApi -> {
            var newPaths = new io.swagger.v3.oas.models.Paths();
            openApi.getPaths().forEach((p, item) -> newPaths.addPathItem("/api" + p, item));
            openApi.setPaths(newPaths);
        };
    }
    private Info apiInfo() {
        return new Info()
                .title("Doctalk API 명세서")
                .description("최근 업데이트 : 2025.04.15 16:35 <br>" +
                        "<b>task/post의 Request Body Schema 확인 할 것<b>"+
                        "<ul><br>" +
                        "  <li>PENDING: 진행 전</li><br>" +
                        "  <li>PROGRESS: 진행 중</li><br>" +
                        "  <li>COMPLETED: 진행 완료</li><br>" +
                        "</ul>")
                .version("1.0.0");
    }

}