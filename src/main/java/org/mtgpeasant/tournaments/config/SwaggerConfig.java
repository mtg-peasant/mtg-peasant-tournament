package org.mtgpeasant.tournaments.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${application.swagger.clientId}")
    private String clientId;

    @Value("${application.swagger.clientSecret}")
    private String clientSecret;

    @Bean
    public Docket swaggerApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("org.mtgpeasant.tournaments.web.api"))
                .paths(PathSelectors.ant("/api/**"))
                .build()
                .useDefaultResponseMessages(false)
                .securitySchemes(Collections.singletonList(securitySchema()))
                .securityContexts(Collections.singletonList(securityContext()))
                .apiInfo(apiInfo());
    }

    private OAuth securitySchema() {
        List<AuthorizationScope> authorizationScopeList = Arrays.asList(new AuthorizationScope("api", "access all APIs"));

        // TODO: retrieve hostname dynamically
        final TokenRequestEndpoint tokenRequestEndpoint = new TokenRequestEndpoint("http://localhost:8080/oauth/authorize", clientId, clientSecret);
        final TokenEndpoint tokenEndpoint = new TokenEndpoint("http://localhost:8080/oauth/token", "access_token");
        AuthorizationCodeGrant authorizationCodeGrant = new AuthorizationCodeGrant(tokenRequestEndpoint, tokenEndpoint);

        List<GrantType> grantTypes = Arrays.asList(authorizationCodeGrant);

        return new OAuth("oauth2", authorizationScopeList, grantTypes);
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder().securityReferences(defaultAuth())
                .forPaths(PathSelectors.ant("/api/**")).build();
    }

    private List<SecurityReference> defaultAuth() {
        return Arrays.asList(new SecurityReference("oauth2", new AuthorizationScope[]{new AuthorizationScope("api", "access all APIs")}));
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().version("1.0")
                .title("Mtg Tournaments API")
                .description("Documentation Mtg Tournaments API v1.0")
                .build();
    }

}
