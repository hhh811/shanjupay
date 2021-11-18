package com.shanjupay.user.api.dto.tenant;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel(value = "AccountDTO", description = "账号信息")
@Data
public class AccountRoleDTO implements Serializable {

    @ApiModelProperty("账号名称")
    private String username;
    @ApiModelProperty("角色编码")
    private String roleCode;
    @ApiModelProperty("租户id")
    private Long tenantId;
}
