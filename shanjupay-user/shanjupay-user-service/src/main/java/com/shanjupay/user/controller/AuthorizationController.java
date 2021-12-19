package com.shanjupay.user.controller;

import com.shanjupay.user.api.AuthorizationService;
import com.shanjupay.user.api.dto.authorization.AuthorizationInfoDTO;
import com.shanjupay.user.api.dto.authorization.PrivilegeDTO;
import com.shanjupay.user.api.dto.authorization.PrivilegeTreeDTO;
import com.shanjupay.user.api.dto.authorization.RoleDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Delete;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Api(value = "同一账号-角色权限", tags = "同一账号-角色权限")
@RestController
public class AuthorizationController {

    @Resource
    private AuthorizationService authService;

    @ApiOperation("授权，获取某用户在多个租户下的权限信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "tenantIds", value = "多个租户的id", required = true, allowMultiple = true, dataType = "Long", paramType = "query", example = "1")
    })
    @GetMapping("/tenants/{username}/privileges")
    public Map<Long, AuthorizationInfoDTO> authorize(@PathVariable String username, @RequestParam Long[] tenantIds) {
        return authService.authorize(username, tenantIds);
    }

    @ApiOperation("查找某租户下，多个角色的权限信息集合")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tenantId", value = "租户id", required = true, dataType = "Long", paramType = "path", example = "1"),
            @ApiImplicitParam(name = "roleCodes", value = "多个角色编码", required = true, allowMultiple = true, dataType = "String", paramType = "query")
    })
    @GetMapping("/tenants/{tenantId}/roles/privilege-list")
    public List<PrivilegeDTO> queryPrivilege(@PathVariable Long tenantId, @RequestParam String[] roleCodes) {
        return authService.queryPrivilege(tenantId, roleCodes);
    }

    @ApiOperation("获取权限组下所有权限")
    @ApiImplicitParam(name = "privilegeGroupId", value = "权限组的id", required = true, dataType = "Long", paramType = "path", example = "1")
    @GetMapping("privilege-groups/{privilegeGroupId}/privilege-list")
    public List<PrivilegeDTO> queryPrivilegeByGroupId(@PathVariable Long privilegeGroupId) {
        return authService.queryPrivilegeByGroupId(privilegeGroupId);
    }

    @ApiOperation("查找某租户下，多个角色的权限信息集合，并根据权限组组装成为权限树")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tenantId", value = "租户id", required = true, dataType = "Long", paramType = "path", example = "1"),
            @ApiImplicitParam(name = "roleCodes", value = "多个角色的编码", required = true, allowMultiple = true, dataType = "String", paramType = "query")
    })
    @GetMapping("/tenants/{tenantId}/roles/role-privilege-list")
    public PrivilegeTreeDTO queryPrivilegeTree(@PathVariable Long tenantId, @RequestParam String[] roleCodes) {
        return authService.queryPrivilegeTree(tenantId, roleCodes);
    }

    //////////////////////////////////////////////////角色、权限///////////////////////////////////////////////////
    @ApiOperation("创建租户内角色（不包含权限）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tenantId", value = "租户id", required = true, dataType = "Long", paramType = "path", example = "1"),
            @ApiImplicitParam(name = "role", value = "角色信息", required = true, dataType = "RoleDTO", paramType = "body")
    })
    @PostMapping("/tenants/{tenantId}/roles")
    public void createRole(@PathVariable Long tenantId, @RequestParam RoleDTO roleDTO) {
        authService.createRole(tenantId, roleDTO);
    }

    @ApiOperation("根据角色编码删除租户内角色，如果有账号绑定该角色，禁止删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tenantId", value = "租户id", required = true, dataType = "Long", paramType = "path", example = "1"),
            @ApiImplicitParam(name = "roleCode", value = "角色编码", required = true, dataType = "String", paramType = "path")
    })
    @Delete("/tenants/{tenantId}/roles/{roleCode}")
    public void removeROle(@PathVariable Long tenantId, @PathVariable String roleCode) {
        authService.removeRole(tenantId, roleCode);
    }

    @ApiOperation("修改租户内角色（不包含权限）")
    @ApiImplicitParam(name = "role", value = "角色信息", required = true, dataType = "RoleDTO", paramType = "body")
    @PutMapping("/tenants/roles")
    public void modifyRole(@RequestParam RoleDTO roleDTO) {
        authService.modifyRole(roleDTO);
    }

    @ApiOperation("角色设置权限（清除+设置）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tenantId", value = "租户id", required = true, dataType = "Long", paramType = "path", example = "1"),
            @ApiImplicitParam(name = "roleCode", value = "角色编码", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "privilegeCodes", value = "权限编码", required = true, allowMultiple = true, dataType = "String", paramType = "query")
    })
    @PutMapping("/tenants/{tenantId}/roles/{roleCode}/privileges")
    public void roleBindPrivilege(@PathVariable Long tenantId, @PathVariable String roleCode, @RequestParam String[] privilegeCodes) {
        authService.roleBindPrivilege(tenantId, roleCode, privilegeCodes);
    }

    @ApiOperation("查询某租户下角色（不分页，不包含权限）")
    @ApiImplicitParam(name = "tenantId", value = "租户id", required = true, dataType = "Long", paramType = "path", example = "1")
    @GetMapping("/tenants/{tenantId}/roles")
    public List<RoleDTO> queryRole(@PathVariable Long tenantId) {
        return authService.queryRole(tenantId);
    }

    @ApiOperation("查询某租户下角色（不分页，不包含权限）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tenantId", value = "租户id", required = true, dataType = "Long", paramType = "path", example = "1"),
            @ApiImplicitParam(name = "roleCodes", value = "角色编码", required = true, allowMultiple = true, dataType = "String", paramType = "query")
    })
    @GetMapping("/tenants/tenants/{tenantId}")
    public List<RoleDTO> queryRole(@PathVariable Long tenantId, @RequestParam String... roleCodes) {
        return authService.queryRole(tenantId, roleCodes);
    }

    @ApiOperation("获取租户内的某个角色信息（包含权限信息）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tenantId", value = "租户id", required = true, dataType = "Long", paramType = "path", example = "1"),
            @ApiImplicitParam(name = "roleCodes", value = "角色编码", required = true, dataType = "String", paramType = "path")
    })
    @GetMapping("/roles/{tenantId}/roles/{roleCode}/role-privilege")
    public RoleDTO queryTenantRole(@PathVariable Long tenantId, @PathVariable String roleCode) {
        return authService.queryTenantRole(tenantId, roleCode);
    }

    @ApiOperation("绑定角色")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "tenantId", value = "租户id", required = true, dataType = "Long", paramType = "path", example = "1"),
            @ApiImplicitParam(name = "roleCodes", value = "角色编码", required = true, allowMultiple = true, dataType = "String", paramType = "query")
    })
    @PostMapping("/bind/tenants/{tenantId}/accounts/roles")
    public void bindAccountRole(@PathVariable String username, @PathVariable Long tenantId, @RequestParam String[] roleCodes) {
        authService.bindAccountRole(username, tenantId, roleCodes);
    }

    @ApiOperation("解绑角色")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "tenantId", value = "租户id", required = true, dataType = "Long", paramType = "path", example = "1"),
            @ApiImplicitParam(name = "roleCodes", value = "角色编码", required = true, allowMultiple = true, dataType = "String", paramType = "query")
    })
    @PutMapping("/unbind/tenants/{tenantId}/accounts/{username}/roles")
    public void unbindAccountRole(@PathVariable String username, @PathVariable Long tenantId, @RequestParam String[] roleCodes) {
        authService.unbindAccountRole(username, tenantId, roleCodes);
    }
}
