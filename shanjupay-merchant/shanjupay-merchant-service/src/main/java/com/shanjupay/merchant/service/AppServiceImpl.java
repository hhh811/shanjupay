package com.shanjupay.merchant.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.common.util.RandomUuidUtil;
import com.shanjupay.merchant.api.AppService;
import com.shanjupay.merchant.api.MerchantService;
import com.shanjupay.merchant.api.dto.AppDTO;
import com.shanjupay.merchant.convert.AppConvert;
import com.shanjupay.merchant.entity.App;
import com.shanjupay.merchant.entity.Merchant;
import com.shanjupay.merchant.mapper.AppMapper;
import com.shanjupay.merchant.mapper.MerchantMapper;
import io.swagger.models.auth.In;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class AppServiceImpl implements AppService {

    @Autowired
    private AppMapper appMapper;

    @Autowired
    private MerchantMapper merchantMapper;

    /**
     * 创建应用
     *
     * @param merchantId 商户id
     * @param app     应用信息
     * @return 创建应用成功的应用信息
     * @throws BusinessException
     */
    @Override
    public AppDTO createApp(Long merchantId, AppDTO app) throws BusinessException {
        // 校验商户是否通过资质审核
        Merchant merchant = merchantMapper.selectById(merchantId);
        if (merchant == null) {
            throw new BusinessException(CommonErrorCode.E_200002);
        }
        if (!"2".equals(merchant.getAuditStatus())) {
            throw new BusinessException(CommonErrorCode.E_200003);
        }

        if (isExistAppName(app.getAppName())) {
            throw new BusinessException(CommonErrorCode.E_200004);
        }

        // 保存应用信息
        app.setAppId(RandomUuidUtil.getUUID());
        app.setMerchantId(merchant.getId());
        App entity = AppConvert.INSTANCE.dto2entity(app);
        appMapper.insert(entity);
        return AppConvert.INSTANCE.entity2dto(entity);
    }

    /**
     * 校验用户名是否已被使用
     * @param appName
     * @return
     */
    public Boolean isExistAppName(String appName) {
        Integer count = appMapper.selectCount((new QueryWrapper<App>().lambda().eq(App::getAppName, appName)));
        return count.intValue() > 0;
    }

    /**
     * 根据商户id查询应用列表
     *
     * @param merchantId
     * @return
     * @throws BusinessException
     */
    @Override
    public List<AppDTO> queryAppByMerchant(Long merchantId) throws BusinessException {
        List<App> apps = appMapper.selectList(new QueryWrapper<App>().lambda().eq(App::getMerchantId, merchantId));
        List<AppDTO> appDTOS = AppConvert.INSTANCE.listentity2dto(apps);
        return appDTOS;
    }

    /**
     * 根据应用id查询应用信息
     *
     * @param appId
     * @return
     * @throws BusinessException
     */
    @Override
    public AppDTO getApplyById(String appId) throws BusinessException {
        App app = appMapper.selectOne(new QueryWrapper<App>().lambda().eq(App::getAppId, appId));
        return AppConvert.INSTANCE.entity2dto(app);
    }

    /**
     * 校验应用是否属于商户
     *
     * @param appId
     * @param merchantId
     * @return
     */
    @Override
    public Boolean queryAppInMerchant(String appId, Long merchantId) {
        return null;
    }
}
