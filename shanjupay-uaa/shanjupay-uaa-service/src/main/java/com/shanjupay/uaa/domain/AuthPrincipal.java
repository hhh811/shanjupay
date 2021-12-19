package com.shanjupay.uaa.domain;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class AuthPrincipal {
    private String username;
    private String domain;
    private String authenticationType;
    private Map<String, Object> payload = new HashMap<>();
}
