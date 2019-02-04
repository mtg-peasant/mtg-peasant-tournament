package org.mtgpeasant.tournaments.config;

import lombok.extern.slf4j.Slf4j;
import org.mtgpeasant.tournaments.service.SsoAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.CompositeFilter;

import javax.servlet.Filter;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableOAuth2Client
@Order(SecurityProperties.BASIC_AUTH_ORDER) // required to install AFTER resource server config
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers("/api/**", "/webjars/**", "/favicon.ico", "/bootstrap/**", "/css/**", "/swagger-resources/**", "/swagger-ui.html", "/v2/api-docs/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // login
        http
                .formLogin()
                .loginPage("/login");

        // authorization
        http.authorizeRequests()
                // unauthorized urls
                .antMatchers("/", "/login", "/login/**", "/about", "/privacypolicy", "/error", "/manage/**")
                .permitAll()
                .and()
                .authorizeRequests()
                .anyRequest()
                .authenticated();

        // install SSO filter before BasicAuthenticationFilter
        http.addFilterBefore(ssoFilter(), BasicAuthenticationFilter.class);

        // and remember me (useful?)
//        http.rememberMe();//
        http.csrf().disable();
    }

    @Autowired
    SsoAuthenticationSuccessHandler ssoAuthenticationSuccessHandler;

    @Autowired
    OAuth2ClientContext oauth2ClientContext;

    @Autowired
    AppProperties appProperties;

    /**
     * Servlet filter that manages SSO from multiple providers
     */
    private Filter ssoFilter() {
        // make composite filter for each provider
        CompositeFilter filter = new CompositeFilter();
        filter.setFilters(appProperties.getSso().entrySet().stream().map(e -> ssoFilter(e.getKey(), e.getValue())).collect(Collectors.toList()));
        return filter;
    }

    /**
     * SSO filter for one provider
     *
     * @param name     provider name
     * @param provider provider configuration
     * @return the servlet filter
     */
    private Filter ssoFilter(String name, AppProperties.SsoProvider provider) {
        OAuth2RestTemplate template = new OAuth2RestTemplate(provider.getClient(), oauth2ClientContext);

        UserInfoTokenServices tokenServices = new UserInfoTokenServices(provider.getResource().getUserInfoUri(), provider.getClient().getClientId());
        tokenServices.setPrincipalExtractor(new EmailExtractor());
        tokenServices.setRestTemplate(template);

        OAuth2ClientAuthenticationProcessingFilter processingFilter = new OAuth2ClientAuthenticationProcessingFilter("/login/" + name);
        processingFilter.setRestTemplate(template);
        processingFilter.setTokenServices(tokenServices);
        processingFilter.setAuthenticationSuccessHandler(ssoAuthenticationSuccessHandler);

        return processingFilter;
    }

    @Bean
    public FilterRegistrationBean oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(filter);
        registration.setOrder(-100);
        return registration;
    }

    /**
     * Use the email address as the user principal
     */
    @Slf4j
    private static class EmailExtractor implements PrincipalExtractor {
        @Override
        public Object extractPrincipal(Map<String, Object> map) {
            log.debug("extractPrincipal from {}", map);
            return map.get("email");
        }
    }
}

