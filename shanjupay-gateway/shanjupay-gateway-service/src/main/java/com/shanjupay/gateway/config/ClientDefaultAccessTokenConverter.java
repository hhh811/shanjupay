package com.shanjupay.gateway.config;

import com.alibaba.fastjson.JSON;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;

import java.util.*;

public class ClientDefaultAccessTokenConverter implements AccessTokenConverter {

    private UserAuthenticationConverter userTokenConverter = new DefaultUserAuthenticationConverter();

    private boolean includeGrantType;

    /**
     * Converter for the part of data in  the token representing a user
     *
     * @param userTokenConverter the userTokenConverter to set
     */
    public void setUserTokenConverter(UserAuthenticationConverter userTokenConverter) {
        this.userTokenConverter = userTokenConverter;
    }

    /**
     * Flag to indicate the grant type should be included in the converted token
     * @param includeGrantType
     */
    public void setIncludedGrantType(boolean includeGrantType) {
        this.includeGrantType = includeGrantType;
    }

    public static final String CLIENT_AUTHORITIES = "client_authorities";

    @Override
    public Map<String, ?> convertAccessToken(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication oAuth2Authentication) {
        Map<String, Object> response = new HashMap<>();
        OAuth2Request clientToken = oAuth2Authentication.getOAuth2Request();

        if (!oAuth2Authentication.isClientOnly()) {
            response.putAll(userTokenConverter.convertUserAuthentication(oAuth2Authentication.getUserAuthentication()));

            // 增加对 client Authorities 支持
            if (clientToken.getAuthorities() != null && !clientToken.getAuthorities().isEmpty()) {
                response.put(CLIENT_AUTHORITIES, AuthorityUtils.authorityListToSet(oAuth2Authentication.getOAuth2Request().getAuthorities()));
            }
        } else {
            if (clientToken.getAuthorities() != null && !clientToken.getAuthorities().isEmpty()) {
                response.put(UserAuthenticationConverter.AUTHORITIES, AuthorityUtils.authorityListToSet(oAuth2Authentication.getOAuth2Request().getAuthorities()));
            }
        }

        if (oAuth2AccessToken.getScope() != null) {
            response.put(SCOPE, oAuth2AccessToken.getScope());
        }
        if (oAuth2AccessToken.getAdditionalInformation().containsKey(JTI)) {
            response.put(JTI, oAuth2AccessToken.getAdditionalInformation().get(JTI));
        }
        if (oAuth2AccessToken.getExpiration() != null) {
            response.put(EXP, oAuth2AccessToken.getExpiration().getTime() / 1000);
        }
        if (includeGrantType && oAuth2Authentication.getOAuth2Request().getGrantType() != null) {
            response.put(GRANT_TYPE, oAuth2Authentication.getOAuth2Request().getGrantType());
        }
        response.putAll(oAuth2AccessToken.getAdditionalInformation());
        response.put(CLIENT_ID, clientToken.getClientId());
        if (clientToken.getResourceIds() != null && !clientToken.getResourceIds().isEmpty()) {
            response.put(AUD, clientToken.getResourceIds());
        }
        return response;
    }

    @Override
    public OAuth2AccessToken extractAccessToken(String s, Map<String, ?> map) {
        DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken(s);
        Map<String, Object> info = new HashMap<>(map);
        info.remove(EXP);
        info.remove(AUD);
        info.remove(CLIENT_ID);
        info.remove(SCOPE);
        if (map.containsKey(EXP)) {
            token.setExpiration((new Date((Long) map.get(EXP) *1000L)));
        }
        if (map.containsKey(JTI)) {
            info.put(JTI, map.get(JTI));
        }
        token.setScope(extractScope(map));
        token.setAdditionalInformation((info));
        return null;
    }

    @Override
    public OAuth2Authentication extractAuthentication(Map<String, ?> map) {
        Map<String, String> parameters = new HashMap<>();
        Set<String> scope = extractScope(map);
        Authentication user = userTokenConverter.extractAuthentication(map);
        String clientId = (String) map.get(CLIENT_ID);
        parameters.put(CLIENT_ID, clientId);
        if (includeGrantType && map.containsKey(GRANT_TYPE)) {
            parameters.put(GRANT_TYPE, (String) map.get(GRANT_TYPE));
        }
        Set<String> resourceIds = new LinkedHashSet<String>(map.containsKey(AUD) ? getAudience(map) : Collections.<String>emptySet());
        Collection<? extends GrantedAuthority> authorities = null;
        if (user == null && map.containsKey(AUTHORITIES)) {
            @SuppressWarnings("unchecked")
            String[] roles = ((Collection<String>) map.get(AUTHORITIES)).toArray(new String[0]);
            authorities = AuthorityUtils.createAuthorityList(roles);
        }
        // 增加对 client Authorities 支持
        if (user != null && map.containsKey(CLIENT_AUTHORITIES)) {
            @SuppressWarnings("unchecked")
            String[] clientRoles = ((Collection<String>) map.get(CLIENT_AUTHORITIES)).toArray(new String[0]);
            authorities = AuthorityUtils.createAuthorityList(clientRoles);
            // 增加额外属性
            parameters.put("mobile", (String) map.get("mobile"));
            parameters.put("payload", JSON.toJSONString(map.get("payload")));
        }

        OAuth2Request request = new OAuth2Request(parameters, clientId, authorities, true, scope, resourceIds, null, null, null);
        return new OAuth2Authentication(request, user);
    }

    private Collection<String> getAudience(Map<String, ?> map) {
        Object auds = map.get(AUD);
        if (auds instanceof Collection) {
            @SuppressWarnings("unchecked")
            Collection<String> result = (Collection<String>) auds;
            return result;
        }
        return Collections.singleton((String) auds);
    }

    private Set<String> extractScope(Map<String, ?> map) {
        Set<String> scope = Collections.emptySet();
        if (map.containsKey(SCOPE)) {
            Object scopeObj = map.get(SCOPE);
            if (String.class.isInstance(scopeObj)) {
                scope = new LinkedHashSet<String>(Arrays.asList(String.class.cast(scopeObj).split(" ")));
            } else if (Collection.class.isAssignableFrom(scope.getClass())) {
                @SuppressWarnings("unchecked")
                Collection<String> scopeColl = (Collection<String>) scopeObj;
                scope = new LinkedHashSet<String>(scopeColl);
            }
        }
        return scope;
    }
}
