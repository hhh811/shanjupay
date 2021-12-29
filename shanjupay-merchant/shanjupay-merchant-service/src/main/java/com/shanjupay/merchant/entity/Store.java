package com.shanjupay.merchant.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("store")
@Data
public class Store {
    @TableId("ID")
    private Long id;
    @TableField("STORE_NAME")
    private String storeName;
    @TableField("STORE_NUMBER")
    private Long storeNumber;
    @TableField("MERCHANT_ID")
    private Long merchantId;
    @TableField("PARENT_ID")
    private Long parentId;
    @TableField("STORE_STATUS")
    private Boolean storeStatus;
    @TableField("STORE_ADDRESS")
    private String storeAddress;

}
