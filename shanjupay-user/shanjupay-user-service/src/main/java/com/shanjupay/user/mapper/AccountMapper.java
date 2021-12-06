package com.shanjupay.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shanjupay.user.api.dto.tenant.AccountDTO;
import com.shanjupay.user.api.dto.tenant.AccountQueryDTO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shanjupay.user.entity.Account;
import com.shanjupay.user.entity.TenantAccount;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountMapper extends BaseMapper<Account> {

    @Select("select count(*) from account where MOBILE = #{mobile} " +
            "and exists (select ACCOUNT_ID ID from tenant_account where TENANT_ID=#{tenantId})")
    int selectAccountInTenantByMobile(@Param("tenantId") Long tenantId, @Param("mobile") String mobile);

    @Select("select count(*) from account where username = #{username} " +
            "and exists (select ACCOUNT_ID ID from tenant_account where TENANT_ID=#{tenantId})")
    int selectAccountInTenantByName(@Param("tenantId") Long tenantId, @Param("username") String username);

    @Select("select count(*) from account account where MOBILE=#{mobile}")
    int selectAccountByMobile(@Param("mobile") String mobile);

    @Select("select count(*) from account account where USERNAME=#{username}")
    int selectAccountByName(@Param("username") String username);

    @Delete("delete from account where ID=#{accountId}")
    void deleteAccount(@Param("accountId") Long accountId);

    @Select("select * from account a, tenant_account t " +
            "where a.ID=t.ACCOUNT_ID " +
            "and TENANT_ID=#{tenantID} " +
            "and a.USERNAME=#{username} " +
            "and t.IS_ADMIN!=1")
    List<TenantAccount> selectNoAdmin(@Param("tenantId") Long tenantId, @Param("username") String username);

    @Select("select * from account a " +
            "LEFT JOIN account_role ar ON a.USERNAME=ar.USERNAME " +
            "LEFT JOIN authorization_role r ON r.`CODE`=ar.ROLE_CODE " +
            "WHERE r.TENANT_ID=#{tenantId} " +
            "AND r.`CODE`=#{roleCode}")
    boolean selectAccountByRole(@Param("tenantID") Long tenantId, @Param("roleCode") String roleCode);

    @Select("<script>" +
            "select * from account a " +
            "left join tenant_account ta on ta.ACCOUNT_ID=a.ID " +
            "left join tenant t on ta.TENANT_ID=t.ID " +
            "<where>" +
            "<if test=\"accountQuery.username != null and accountQuery.username != ''\"> " +
            "and a.NAME=#{tenantQuery.name} " +
            "</if>" +
            "<if test=\"tenantQuery.mobile != null and tenantQuery.mobile != ''\"> " +
            "and a.MOBILE=#{tenantQuery.mobile} " +
            "</if>" +
            "<if test=\"tenantQuery.tenantId != null and tenantQuery.tenantId != ''\"> " +
            "and t.ID in #{tenantQuery.tenantId} " +
            "</if>" +
            "</where>" +
            "ORDERED BY #{sortBy} #{order} " +
            "</script>")
    List<AccountDTO> selectAccountByPage(@Param("page") Page<AccountDTO> page, @Param("accountQuery")AccountQueryDTO accountQuery,
                                         @Param("sortBy") String sortBy, @Param("order") String order);

    @Select("select a.id from account a " +
            "left join tenant_account ta on ta.ACCOUNT_ID=a.ID " +
            "left join tenant t on t.ID=ta.TENANT_ID " +
            "where t.ID=#{tenantId}")
    List<Long> selectAccountByTenantId(@Param("tenantId") Long tenantId);

    @Select("select a.* from account a " +
            "join tenant_account ta on ta.ACCOUNT_ID=a.ID " +
            "join tenant t on t.ID=a.TENANT_ID " +
            "where t.id=#{tenantId} " +
            "AND ta.`ID_ADMIN`=1")
    Account selectAccountInfoByTenantId(@Param("tenantId") Long tenantId);
}
