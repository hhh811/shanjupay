package com.shanjupay.merchant.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("store_staff")
@Data
public class StoreStaff {
    @TableId("ID")
    private Long id;
    @TableField("STORE_ID")
    private Long storeId;
    @TableField("STAFF_ID")
    private Long staffId;
}
