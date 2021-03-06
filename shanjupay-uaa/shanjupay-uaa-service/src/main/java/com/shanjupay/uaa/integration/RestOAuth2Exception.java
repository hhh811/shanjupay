package com.shanjupay.uaa.integration;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

@JsonSerialize(using = RestOAuthExceptionJacksonSerializer.class)
public class RestOAuth2Exception extends OAuth2Exception {
    public RestOAuth2Exception(String msg, Throwable throwable) {
        super(msg, throwable);
    }

    public RestOAuth2Exception(String msg) {
        super(msg);
    }
}
