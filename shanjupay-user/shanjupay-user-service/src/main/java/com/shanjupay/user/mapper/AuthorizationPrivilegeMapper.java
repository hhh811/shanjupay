package com.shanjupay.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shanjupay.user.api.dto.tenant.TenantRolePrivilegeDTO;
import com.shanjupay.user.entity.AuthorizationPrivilege;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorizationPrivilegeMapper extends BaseMapper<AuthorizationPrivilege> {

    @Select("<script>" +
            "SELECT p.* FROM authorization_privilege p \n" +
            "\tLEFT JOIN authorization_role_privilege arp ON arp.PRIVILEGE_ID=p.ID\n" +
            "\tLEFT JOIN authorization_role ON arp.ROLE_ID=r.ID\n" +
            "\tWHERE r.ID IN <foreach collection='roleIds' item='item' open='(' separator=',' close=')'>#{item}</foreach>" +
            "</script>")
    List<AuthorizationPrivilege> selectPrivilegeByRole(@Param("roleIds") List<Long> roleIds);

    @Select("<script>" +
            "select r.TENANT_ID, `CODE`, ROLE_CODE, p.`CODE` PRIVILEGE from authorization_privilege p " +
            "LEFT JOIN authorization_role_privilege arp ON arp.PRIVILEGE_ID=p.ID " +
            "LEFT JOIN authorization_role r ON arp.ROLE_ID=r.ID " +
            "where r.ID IN <foreach collection='roleIds' item='item' open='(' separator=',' close=')'>#{item}</foreach> " +
            "ORDER BY r.TENANT_ID " +
            "</script>")
    List<TenantRolePrivilegeDTO> selectPrivilegeRoleInTenant(@Param("roleIds") List<Long> roleIds);
}
