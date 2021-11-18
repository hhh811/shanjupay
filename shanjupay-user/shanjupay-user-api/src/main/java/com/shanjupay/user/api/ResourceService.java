package com.shanjupay.user.api;

import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.common.domain.PageVO;
import com.shanjupay.user.api.dto.authorization.AuthorizationInfoDTO;
import com.shanjupay.user.api.dto.resource.ApplicationDTO;
import com.shanjupay.user.api.dto.resource.ApplicationQueryParams;
import com.shanjupay.user.api.dto.resource.ResourceDTO;

import java.util.List;
import java.util.Map;

public interface ResourceService {

    /**
     * 创建议应用
     * 会关联创建uaa服务中的接入客户端，其中code为clientId
     * @param application
     * @throws BusinessException
     */
    void createApplication(ApplicationDTO application) throws BusinessException;

    /**
     * 修改应用
     * 仅仅可以修改名称
     * @param application
     * @throws BusinessException
     */
    void modifyApplication(ApplicationDTO application) throws BusinessException;

    /**
     * 删除应用
     * 关联删除uaa服务中的接入客户端，若应用下有资源，禁止删除
     * @param applicationCode
     * @throws BusinessException
     */
    void removeApplication(String applicationCode) throws BusinessException;

    /**
     * 根据应用编码查询应用
     * @param applicationCode
     * @return
     * @throws BusinessException
     */
    ApplicationDTO queryApplication(String applicationCode) throws BusinessException;

    /**
     * 条件分页查找应用
     * @param query
     * @param pageNo
     * @param pageSize
     * @return
     * @throws BusinessException
     */
    PageVO<ApplicationDTO> pageApplicationByCondition(ApplicationQueryParams query, Integer pageNo, Integer pageSize) throws BusinessException;

    /**
     * 根据权限加载指定应用的资源
     * @param privilegeCodes
     * @param applicationCode
     * @return
     * @throws BusinessException
     */
    List<ResourceDTO> loadResources(List<String> privilegeCodes, String applicationCode) throws BusinessException;

    /**
     * 获取多个租户下权限所对应的资源信息，并按应用拆分
     * @param tenantAuthorizationInfoMap    租户id->角色权限信息
     * @return  租户id->多个应用的资源嘻嘻
     * @throws BusinessException
     */
    Map<Long, List<ResourceDTO>> loadResources(Map<Long, AuthorizationInfoDTO> tenantAuthorizationInfoMap) throws BusinessException;
}
