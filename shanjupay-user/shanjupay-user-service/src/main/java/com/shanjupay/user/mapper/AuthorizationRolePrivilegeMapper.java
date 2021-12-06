package com.shanjupay.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shanjupay.user.entity.AuthorizationRolePrivilege;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorizationRolePrivilegeMapper extends BaseMapper<AuthorizationRolePrivilege> {
    @Insert("<script>" +
            "INSERT INTO authorization_role_privilege(ROLE_ID,PRIVILEGE_ID) VALUES " +
            "<foreach collection='pids' item='item' separator=','>(#{rid},#{item})</foreach> " +
            "</script>")
    void insertRolePrivilege(@Param("rid") Long rid, @Param("pids") List<Long> pids);
}
