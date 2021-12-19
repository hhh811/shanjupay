package com.shanjupay.uaa.domain;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

public class UnifiedUserDetails implements UserDetails {
    private static final long serialVersionUID = 3957586021470480642L;

    protected List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

    private String username;

    private String password;

    private String mobile;

    private Map<Long, Object> payload = new HashMap<>();

    private Map<String, Object> tenant = new HashMap<>();

    public UnifiedUserDetails(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public UnifiedUserDetails(String username, String password, Map<Long, Object> payload) {
        this.username = username;
        this.password = password;
        this.payload = payload;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return this.grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public Map<Long, Object> getPayload() {
        return payload;
    }

    public void setPayload(Map<Long, Object> payload) {
        this.payload = payload;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Map<String, Object> getTenant() {
        return tenant;
    }

    public void setTenant(Map<String, Object> tenant) {
        this.tenant = tenant;
    }
}
