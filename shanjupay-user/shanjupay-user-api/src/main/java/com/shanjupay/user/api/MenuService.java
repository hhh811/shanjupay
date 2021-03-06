package com.shanjupay.user.api;

import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.common.domain.PageVO;
import com.shanjupay.user.api.dto.menu.MenuDTO;
import com.shanjupay.user.api.dto.menu.MenuQueryDTO;

import java.util.List;

/**
 * 菜单服务
 * 菜单为手工建立维护
 */
public interface MenuService {

    /**
     * 根据id查询菜单
     * @param id
     * @return
     * @throws BusinessException
     */
    MenuDTO queryMenu(Long id) throws BusinessException;

    /**
     * 根据应用编码查询菜单列表
     * @param applicationCode
     * @return
     * @throws BusinessException
     */
    List<MenuDTO> queryMenuByApplicationCode(String applicationCode) throws BusinessException;

    /**
     * 根据条件查询菜单列表
     * @param params
     * @param pageNo
     * @param pageSize
     * @return
     * @throws BusinessException
     */
    PageVO<MenuDTO> queryMenu(MenuQueryDTO params, Integer pageNo, Integer pageSize) throws BusinessException;

    /**
     * 根据权限编码列表获取菜单
     * @param privileges
     * @return
     * @throws BusinessException
     */
    List<MenuDTO> queryMenuByPrivileges(String[] privileges) throws BusinessException;
}
