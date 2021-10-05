package com.shanjupay.merchant.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("merchant")
public class Merchant {

    @TableId("ID")
    private Long id;
    @TableField("MERCHANT_NAME")
    private String merchantName;
    @TableField("MERCHANT_NO")
    private String merchantNo;
    @TableField("MERCHANT_ADDRESS")
    private String merchantAddress;
    @TableField("MERCHANT_TYPE")
    private String merchantType;
    @TableField("BUSINESS_LICENSES_IMG")
    private String businessLicenseImg;
    @TableField("ID_CARD_FRONT_IMG")
    private String idCardFrontImg;
    @TableField("ID_CARD_AFTER_IMG")
    private String idCardAfterImg;
    @TableField("USERNAME")
    private String userName;
    @TableField("MOBILE")
    private String mobile;
    @TableField("CONTACTS_ADDRESS")
    private String contactAddress;
    @TableField("AUDIT_STATUS")
    private String auditStatus;
    @TableField("TENANT_ID")
    private Long tenantId;
}
