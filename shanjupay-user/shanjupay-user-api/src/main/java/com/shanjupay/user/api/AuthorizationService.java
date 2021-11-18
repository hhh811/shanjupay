package com.shanjupay.user.api;

import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.common.domain.PageVO;
import com.shanjupay.user.api.dto.authorization.AuthorizationInfoDTO;
import com.shanjupay.user.api.dto.authorization.PrivilegeDTO;
import com.shanjupay.user.api.dto.authorization.PrivilegeTreeDTO;
import com.shanjupay.user.api.dto.authorization.RoleDTO;

import java.util.List;
import java.util.Map;

/**
 * 授权服务：负责 用户-角色关系、角色、权限、等信息维护
 *
 * 权限组、权限为手工维护
 *
 * 租户内角色数量、租户内账号数量、应用数量可用套餐限制
 */
public interface AuthorizationService {

    /**
     * 授权，获取某个用户在多租户下的授权信息
     * @param username
     * @param tenantIds
     * @return
     * @throws BusinessException
     */
    Map<Long, AuthorizationInfoDTO> authorize(String username, Long[] tenantIds) throws BusinessException;

    /**
     * 查找某租户下，多个角色的授权信息集合
     * @param tenantId
     * @param roleCodes
     * @return
     * @throws BusinessException
     */
    List<PrivilegeDTO> queryPrivilege(Long tenantId, String[] roleCodes) throws BusinessException;

    /**
     * 获取权限组下所有权限
     * @param privilegeGroupId
     * @return
     * @throws BusinessException
     */
    List<PrivilegeDTO> queryPrivilegeByGroupId(Long privilegeGroupId) throws BusinessException;

    /**
     * 查找某租户下多个角色权限信息集合，并根据权限组组成为权限树
     * @param tenantId
     * @param roleCodes
     * @return
     * @throws BusinessException
     */
    PrivilegeTreeDTO queryPrivilegeTree(Long tenantId, String[] roleCodes) throws BusinessException;

    //////////////////////////////////////////////////角色、权限///////////////////////////////////////////////////

    /**
     * 创建租户内角色（不包含权限）
     * @param tenantId
     * @param role
     * @throws BusinessException
     */
    void createRole(Long tenantId, RoleDTO role) throws BusinessException;

    /**
     * 删除租户内角色，如果有账号绑定该角色，禁止删除
     * @param tenantId
     * @param roleCOde
     * @throws BusinessException
     */
    void removeRole(Long tenantId, String roleCOde) throws BusinessException;

    /**
     * 删除租户内角色
     * @param id
     * @throws BusinessException
     */
    void removeRole(Long id) throws BusinessException;

    /**
     * 修改租户内角色（不包含权限）
     * @param role
     * @throws BusinessException
     */
    void modifyRole(RoleDTO role) throws BusinessException;

    /**
     * 角色权限设置（清除+设置）
     * @param tenantId
     * @param roleCode
     * @param prilivegeCodes
     * @throws BusinessException
     */
    void roleBindPrivilege(Long tenantId, String roleCode, String[] prilivegeCodes) throws BusinessException;

    /**
     * 查询租户下角色（不分页，不包含权限）
     * @param tenantId
     * @return
     */
    List<RoleDTO> queryRole(Long tenantId);

    /**
     * 根据roleCode获取角色（不包含权限）
     * @param tenantId
     * @param roleCodes
     * @return
     * @throws BusinessException
     */
    List<RoleDTO> queryRole(Long tenantId, String ...roleCodes) throws BusinessException;

    /**
     * 获取租户内某个角色信息（包含权限信息）
     * @param tenantId
     * @param roleCode
     * @return
     * @throws BusinessException
     */
    RoleDTO queryTenantRole(Long tenantId, String roleCode) throws BusinessException;

    /**
     * 绑定角色
     * @param username
     * @param tenantId
     * @param roleCodes
     * @throws BusinessException
     */
    void bindAccountRole(String username, Long tenantId, String[] roleCodes) throws BusinessException;

    /**
     * 解绑角色
     * @param username
     * @param tenantId
     * @param roleCodes
     * @throws BusinessException
     */
    void unbindAccountRole(String username, Long tenantId, String[] roleCodes) throws BusinessException;

    /**
     * 分页查询角色
     * @param roleDTO
     * @param pageNo
     * @param pageSize
     * @return
     * @throws BusinessException
     */
    PageVO<RoleDTO> queryRoleByPage(RoleDTO roleDTO, Integer pageNo, Integer pageSize) throws BusinessException;

    /**
     * 判断账号是否绑定了某些角色
     * @param username
     * @param tenantId
     * @param roleCodes
     * @return
     * @throws BusinessException
     */
    List<AccountRoleDTO> queryAccountBindRole(String username, Long tenantId, String[] roleCodes) throws BusinessException;

    /**
     * 根据租户内的账号查询绑定的角色
     * @param username
     * @param tenantId
     * @return
     */
    List<AccountRoleDTO> queryAccountRole(String username, Long tenantId);

    /**
     * 根据租户内账号查询绑定的角色列表
     * @param username
     * @param tenantId
     * @return
     */
    List<RoleDTO> queryRolesByUsername(String username, Long tenantId);
}
