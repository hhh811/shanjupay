package com.shanjupay.uaa.config;

import com.shanjupay.uaa.integration.IntegrationUserDetailAuthenticationHandler;
import com.shanjupay.uaa.integration.IntegrationUserDetailAuthenticationProvider;
import com.shanjupay.user.api.AuthorizationService;
import com.shanjupay.user.api.TenantService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Reference
    private TenantService tenantService;

    @Reference
    private AuthorizationService authorizationService;

    @Bean
    public IntegrationUserDetailAuthenticationHandler integrationUserDetailAuthenticationHandler() {
        IntegrationUserDetailAuthenticationHandler authenticationHandler = new IntegrationUserDetailAuthenticationHandler();
        authenticationHandler.setTenantService(tenantService);
        return authenticationHandler;
    }

    @Bean
    public IntegrationUserDetailAuthenticationProvider integrationUserDetailAuthenticationProvider() {
        return new IntegrationUserDetailAuthenticationProvider(integrationUserDetailAuthenticationHandler());
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(integrationUserDetailAuthenticationProvider());
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/public/**", "/webjars/**", "/v2/**", "/swagger/**", "/static/**", "/resources/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/login*").permitAll()
                .antMatchers("/logout*").permitAll()
                .antMatchers("/druid/**").permitAll()
                .anyRequest().authenticated()
                .and().formLogin()
                .loginPage("/login")
                .loginPage("/login")
                .loginProcessingUrl("/login.do")
                .failureUrl("/login?authentication_error=1")
                .defaultSuccessUrl("/oauth/authorize")
                .usernameParameter("username")
                .passwordParameter("password")
                .and().logout()
                .logoutUrl("/logout.do")
                .deleteCookies("JSESSIONID")
                .logoutSuccessUrl("/")
                .and().csrf().disable()
                .exceptionHandling()
                .accessDeniedPage("/login?authorization_error=2");
    }
}
