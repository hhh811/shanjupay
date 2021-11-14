package com.shanjupay.uaa.api.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class MerchantLoginDTO implements Serializable {
    private String mobile;

    private String name;

    private Map<String, Object> payload;

    private int effectiveTime;
}
