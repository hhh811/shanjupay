package com.shanjupay.uaa.config;

import com.shanjupay.uaa.integration.ClientDefaultAccessTokenConverter;
import com.shanjupay.uaa.integration.UnifiedUserAuthenticationConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
public class JWTConfig {
    @Value("${signing-key")
    private String SIGNING_KEY;

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey(SIGNING_KEY);
        ClientDefaultAccessTokenConverter accessTokenConverter = new ClientDefaultAccessTokenConverter();
        accessTokenConverter.setUserTokenConverter(new UnifiedUserAuthenticationConverter());
        converter.setAccessTokenConverter(accessTokenConverter);
        return converter;
    }
}
