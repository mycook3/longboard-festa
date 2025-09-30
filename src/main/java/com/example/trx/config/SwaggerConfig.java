package com.example.trx.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI api(){
        Info info=new Info()
                .title("")
                .description("");
        return new OpenAPI()
                .components(new Components())
                .info(info);
    }

    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder().group("users").pathsToMatch("/api/users/**").build();
    }

    @Bean
    public GroupedOpenApi noticeApi() {
        return GroupedOpenApi.builder().group("notices").pathsToMatch("/api/v1/notices/**").build();
    }

    @Bean
    public GroupedOpenApi accountApi() {
        return GroupedOpenApi.builder().group("accounts").pathsToMatch("/api/accounts/**").build();
    }

    @Bean
    public GroupedOpenApi transferApi() {
        return GroupedOpenApi.builder().group("transfer").pathsToMatch("/api/transfers/**").build();
    }

    @Bean
    public GroupedOpenApi scheduledTransferApi() {
        return GroupedOpenApi.builder()
            .group("scheduled-transfers")
            .pathsToMatch("/api/scheduled-transfers/**")
            .build();
    }


}
