package com.shanjupay.merchant.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("staff")
public class Staff {

    @TableId("ID")
    private Long id;
    @TableField("MERCHANT_ID")
    private Long merchantId;
    @TableField("FULL_NAME")
    private String fullName;
    @TableField("POSITION")
    private String position;
    @TableField("USERNAME")
    private String userName;
    @TableField("MOBILE")
    private String mobile;
    @TableField("STORE_ID")
    private Long storeId;
    @TableField("LAST_LOGIN_TIME")
    private LocalDateTime lastLoginTime;
    @TableField("STAFF_STATUS")
    private Integer staffStatus;
}
