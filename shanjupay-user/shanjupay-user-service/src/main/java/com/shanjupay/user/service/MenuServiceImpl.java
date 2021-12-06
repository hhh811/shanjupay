package com.shanjupay.user.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shanjupay.common.domain.PageVO;
import com.shanjupay.user.api.MenuService;
import com.shanjupay.user.api.dto.menu.MenuDTO;
import com.shanjupay.user.api.dto.menu.MenuQueryDTO;
import com.shanjupay.user.convert.ResourceMenuConvert;
import com.shanjupay.user.entity.ResourceMenu;
import com.shanjupay.user.mapper.ResourceMenuMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class MenuServiceImpl implements MenuService {

    @Autowired
    private ResourceMenuMapper resourceMenuMapper;

    /**
     * 根据ID查询结果
     * @param id
     * @return
     */
    @Override
    public MenuDTO queryMenu(Long id) {
        ResourceMenu resourceMenu = resourceMenuMapper.selectById(id);
        return ResourceMenuConvert.INSTANCE.entity2dto(resourceMenu);
    }

    /**
     * 根据应用编码查询菜单列表
     * @param applicationCode
     * @return
     */
    @Override
    public List<MenuDTO> queryMenuByApplicationCode(String applicationCode) {
        List<ResourceMenu> resourceMenus = resourceMenuMapper.selectList(new QueryWrapper<ResourceMenu>().lambda()
                .eq(ResourceMenu::getApplicationCode, applicationCode));
        return ResourceMenuConvert.INSTANCE.entitylist2dto(resourceMenus);
    }

    /**
     * 根据条件查询菜单列表
     * @param params
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Override
    public PageVO<MenuDTO> queryMenu(MenuQueryDTO params, Integer pageNo, Integer pageSize) {
        //参数 applicationCode (app表) title status (菜单表)
        pageSize = (pageSize == null ? 20 : pageSize);
        Page<MenuDTO> page = new Page<>(pageNo, pageSize);  //当前页, 总条数 构造page对象
        List<MenuDTO> menus = resourceMenuMapper.selectByPage(page, params);
        return new PageVO<>(menus, page.getTotal(), pageNo, pageSize);
    }

    /**
     * 根据权限编码列表获取菜单
     * @param privileges
     * @return
     */
    @Override
    public List<MenuDTO> queryMenuByPrivileges(String[] privileges) {
        List<String> privilege = Arrays.asList(privileges);
        List<ResourceMenu> resourceMenus = resourceMenuMapper.selectList(new QueryWrapper<ResourceMenu>().lambda()
                .in(ResourceMenu::getPrivilegeCode, privilege));
        return ResourceMenuConvert.INSTANCE.entitylist2dto(resourceMenus);
    }
}
