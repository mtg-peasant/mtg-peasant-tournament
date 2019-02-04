package org.mtgpeasant.tournaments.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ConfigurationProperties("application")
@Data
public class AppProperties {
    private final OAuthAppConfig oauth = new OAuthAppConfig();

    private final Map<String, SsoProvider> sso = new HashMap<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OAuthAppConfig {
        private String signingKey;
        private String verifierKey;
        public final List<OAuth2ClientDetails> clients = new ArrayList<>();

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class OAuth2ClientDetails {
            private String clientId;
            private String clientSecret;
            private String[] grantTypes = {};
            private String[] authorities = {};
            private String[] scopes = {};
            private boolean autoApprove = false;
            private String[] redirectUris = null;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SsoProvider {
        private AuthorizationCodeResourceDetails client;
        private ResourceServerProperties resource;
    }
}
