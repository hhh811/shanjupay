package com.shanjupay.merchant.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("app")
public class App {

    @TableId("ID")
    private Long id;
    @TableField("APP_ID")
    private String appId;
    @TableField("APP_NAME")
    private String appName;
    @TableField("MERCHANT_ID")
    private Long merchantId;
    @TableField("PUBLIC_KEY")
    private String publicKey;
    @TableField("NOTIFY_URL")
    private String notifyUrl;
}
