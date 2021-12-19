package com.shanjupay.uaa.integration;

import com.alibaba.fastjson.JSON;
import com.shanjupay.uaa.domain.AuthPrincipal;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Map;

/**
 * 统一用户认证处理，集成了网页(简化模式、授权码模式用户登录)认证  与  password模式认证
 */
public class IntegrationUserDetailAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {
    private IntegrationUserDetailAuthenticationHandler authenticationHandler = null;

    public IntegrationUserDetailAuthenticationProvider(IntegrationUserDetailAuthenticationHandler authenticationHandler) {
        this.authenticationHandler = authenticationHandler;
    }

    @SuppressWarnings("deprecation")
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authenticationToken)
            throws AuthenticationException {
    }

    protected final UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authenticationToken)
            throws AuthenticationException {
        try {
            UserDetails loadedUser = authenticationUser(authenticationToken);
            if (loadedUser == null) {
                throw new InternalAuthenticationServiceException("UserDetailsService returned null, which is an interface contract violation");
            }
            return loadedUser;
        } catch (UsernameNotFoundException exception) {
            throw exception;
        } catch (Exception e) {
            throw new InternalAuthenticationServiceException(e.getMessage(), e);
        }
    }

    private UserDetails authenticationUser(UsernamePasswordAuthenticationToken authenticationToken) {
        if (authenticationToken.getPrincipal() == null) {
            throw new BadCredentialsException("username is blank");
        }
        String username = authenticationToken.getName();
        if (authenticationToken.getCredentials() == null) {
            throw new BadCredentialsException("Credentials is blank");
        }
        String credentials = authenticationToken.getCredentials().toString();

        AuthPrincipal authPrincipal = null;

        try {
            authPrincipal = JSON.parseObject(username, AuthPrincipal.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadCredentialsException("username parseObject error");
        }

        if (authenticationToken.getDetails() instanceof Map) {
            Map detailsMap = (Map) authenticationToken.getDetails();
            authPrincipal.getPayload().putAll(detailsMap);
        }

        return authenticationHandler.authentication(authPrincipal, credentials);
    }
}
