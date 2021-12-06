package com.shanjupay.user.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.common.domain.PageVO;
import com.shanjupay.user.api.ResourceService;
import com.shanjupay.user.api.dto.authorization.AuthorizationInfoDTO;
import com.shanjupay.user.api.dto.resource.ApplicationDTO;
import com.shanjupay.user.api.dto.resource.ApplicationQueryParams;
import com.shanjupay.user.api.dto.resource.ResourceDTO;
import com.shanjupay.user.convert.ResourceApplicationConvert;
import com.shanjupay.user.entity.ResourceApplication;
import com.shanjupay.user.entity.ResourceButton;
import com.shanjupay.user.entity.ResourceMenu;
import com.shanjupay.user.mapper.ResourceApplicationMapper;
import com.shanjupay.user.mapper.ResourceButtonMapper;
import com.shanjupay.user.mapper.ResourceMenuMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.*;

@Service
@Slf4j
public class ResourceServiceImpl implements ResourceService {

    @Autowired
    private ResourceApplicationMapper applicationMapper;

    @Autowired
    private ResourceMenuMapper menuMapper;

    @Autowired
    private ResourceButtonMapper buttonMapper;

    /**
     * 创建应用
     * @param application
     */
    @Override
    public void createApplication(ApplicationDTO application) {
        ResourceApplication entity = ResourceApplicationConvert.INSTANCE.dto2entity(application);
        applicationMapper.insert(entity);
    }

    /**
     * 修改应用
     * 仅仅可以修改名称
     * @param application
     */
    @Override
    public void modifyApplication(ApplicationDTO application) {
        Assert.notNull(application, "对象不能为空");
        ResourceApplication entity = ResourceApplicationConvert.INSTANCE.dto2entity(application);
        applicationMapper.updateById(entity);
    }

    /**
     * 删除应用
     * 关联删除uaa服务中的接入客户端，若应用下游资源，禁止删除
     * @param applicationCode
     */
    @Override
    public void removeApplication(String applicationCode) {
        // 查询应用下是否有资源
        int i = menuMapper.selectResourceByapplicationCode(applicationCode);
        Assert.isTrue(i > 0, "应用下有关联资源, 不能删除");
        applicationMapper.delete(new QueryWrapper<ResourceApplication>().lambda().eq(ResourceApplication::getCode, applicationCode));
    }

    /**
     * 根据应用编码查找应用
     * @param applicationCode
     * @return
     */
    @Override
    public ApplicationDTO queryApplication(String applicationCode) {
        ResourceApplication application = applicationMapper.selectOne(new QueryWrapper<ResourceApplication>().lambda()
                .eq(ResourceApplication::getCode, applicationCode));
        return ResourceApplicationConvert.INSTANCE.entity2dto(application);
    }

    /**
     * 条件分页查找应用
     * @param query
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Override
    public PageVO<ApplicationDTO> pageApplicationByConditions(ApplicationQueryParams query, Integer pageNo, Integer pageSize) {
        Page<ApplicationDTO> page = new Page<>(pageNo, pageSize);
        List<ApplicationDTO> apps = applicationMapper.selectAppByPage(page, query);
        return new PageVO<>(apps, page.getTotal(), pageNo, pageSize);
    }

    /**
     * 根据权限加载指定资源
     * @param privilegeCodes
     * @param applicationCode
     * @return
     */
    public List<ResourceDTO> loadResources(List<String> privilegeCodes, String applicationCode) {
        //1.获取应用的全部信息
        ResourceApplication app = applicationMapper.selectOne(new QueryWrapper<ResourceApplication>()
                .lambda().eq(ResourceApplication::getCode, applicationCode));
        //2.获取应用下的菜单资源
        List<ResourceMenu> resourceMenus = menuMapper.selectList(new QueryWrapper<ResourceMenu>().lambda()
                .eq(ResourceMenu::getApplicationCode, applicationCode).in(ResourceMenu::getPrivilegeCode, privilegeCodes));
        String menus = JSON.toJSONString(resourceMenus);
        //3.获取应用下的按钮资源
        List<ResourceButton> resourceButtons = buttonMapper.selectList(new QueryWrapper<ResourceButton>()
                .lambda().eq(ResourceButton::getApplicationCode, app).in(ResourceButton::getPrivilegeCode, privilegeCodes));
        String buttons = JSON.toJSONString(resourceButtons);
        //4.组装应用的资源数据
        Map<String, Object> appRes = new HashMap<>();
        if (!resourceMenus.isEmpty()) {
            appRes.put("menu", menus);
        }
        if (!resourceButtons.isEmpty()) {
            appRes.put("button", buttons);
        }
        ResourceDTO resource = new ResourceDTO();
        resource.setApplicationName(app.getName());
        resource.setApplicationCode(app.getCode());
        resource.setAppRes(appRes);
        List<ResourceDTO> resourceDTOList = new ArrayList<>();
        resourceDTOList.add(resource);
        return resourceDTOList;
    }

    //获取多个租户下权限所对应的的资源信息，并按应用拆分
    //入参：多个租户下的角色权限信息，key为租户id  value为租户下的角色权限信息
    //返回值：{租户A:[{应用1资源信息},{应用2资源信息}],租户B:[{},{}...]}
    @Override
    public Map<Long, List<ResourceDTO>> loadResources(Map<Long, AuthorizationInfoDTO> tenantAuthorizationInfoMap) {
        if (tenantAuthorizationInfoMap.isEmpty()) {
            throw new BusinessException(CommonErrorCode.E_110006);
        }
        Map<Long, List<ResourceDTO>> resultMap = new HashMap<>();
        //遍历某用户在多个租户下的权限信息
        for (Map.Entry<Long, AuthorizationInfoDTO> entry : tenantAuthorizationInfoMap.entrySet()) {
            Long tenantId = entry.getKey();
            AuthorizationInfoDTO authInfo = entry.getValue();
            //遍历角色权限map
            Set<String> privilegeCodeSet = new HashSet<>();
            authInfo.getRolePrivilegeMap().values().forEach(privilegeCodeSet::addAll);
            //根据权限编码获取对应的资源(菜单和按钮)
            List<ResourceMenu> resourceMenus = menuMapper.selectList(new QueryWrapper<ResourceMenu>().lambda()
                    .in(ResourceMenu::getPrivilegeCode, privilegeCodeSet));
            List<ResourceButton> resourceButtons = buttonMapper.selectList(new QueryWrapper<ResourceButton>().lambda()
                    .in(ResourceButton::getPrivilegeCode, privilegeCodeSet));
            //按照应用划分 组装资源
            List<ResourceDTO> resourceDTOList = new ArrayList<>();
            Map<String, List<ResourceMenu>> tempMenuMap = new HashMap<>();
            resourceMenus.forEach(m -> {
                tempMenuMap.computeIfAbsent(m.getApplicationCode(), c -> new ArrayList<>());
                tempMenuMap.get(m.getApplicationCode()).add(m);
            });
            Map<String, List<ResourceMenu>> tempButtonMap = new HashMap<>();
            resourceMenus.forEach(b -> {
                tempButtonMap.computeIfAbsent(b.getApplicationCode(), c -> new ArrayList<>());
                tempButtonMap.get(b.getApplicationCode()).add(b);
            });
            for (Map.Entry<String, List<ResourceMenu>> tEntry : tempMenuMap.entrySet()) {
                ResourceDTO resourceDTO = new ResourceDTO();
                resourceDTO.setApplicationCode(tEntry.getKey());
                Map map1 = new HashMap();
                map1.put("menu", tEntry.getValue());
                if (tempButtonMap.get(tEntry.getKey()) != null) {
                    map1.put("button", tempButtonMap.get(tEntry.getKey()));
                }
                resourceDTO.setAppRes(map1);
                resourceDTOList.add(resourceDTO);
            }
            resultMap.put(tenantId, resourceDTOList);
        }
        return resultMap;
    }
}
