package com.shanjupay.user.api.dto.authorization;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 角色信息
 */
@ApiModel(value = "RoleDTO", description = "角色信息")
@Data
public class RoleDTO {
    @ApiModelProperty("角色id")
    private Long id;

    @ApiModelProperty("角色名称")
    private String name;

    @ApiModelProperty("角色编码")
    private String code;

    @ApiModelProperty("角色所属租户id")
    private Long tenantId;

    @ApiModelProperty("角色包含权限列表")
    private List<String> privilegeCodes = new ArrayList<>();
}
