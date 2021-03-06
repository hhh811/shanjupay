package com.shanjupay.transaction.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.additional.query.impl.LambdaQueryChainWrapper;
import com.shanjupay.common.cache.Cache;
import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.common.util.RedisUtil;
import com.shanjupay.transaction.api.PayChannelService;
import com.shanjupay.transaction.api.dto.PayChannelDTO;
import com.shanjupay.transaction.api.dto.PayChannelParamDTO;
import com.shanjupay.transaction.api.dto.PlatformChannelDTO;
import com.shanjupay.transaction.convert.PayChannelParamConvert;
import com.shanjupay.transaction.convert.PlatformChannelConvert;
import com.shanjupay.transaction.entity.AppPlatformChannel;
import com.shanjupay.transaction.entity.PayChannelParam;
import com.shanjupay.transaction.entity.PlatformChannel;
import com.shanjupay.transaction.mapper.AppPlatformChannelMapper;
import com.shanjupay.transaction.mapper.PayChannelMapper;
import com.shanjupay.transaction.mapper.PayChannelParamMapper;
import com.shanjupay.transaction.mapper.PlatformChannelMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Locale;

@Service
public class PayChannelServiceImpl implements PayChannelService {

    @Autowired
    private PlatformChannelMapper platformChannelMapper;

    @Autowired
    private AppPlatformChannelMapper appPlatformChannelMapper;

    @Autowired
    private PayChannelMapper payChannelMapper;

    @Autowired
    private PayChannelParamMapper payChannelParamMapper;

    @Autowired
    private Cache cache;

    /**
     * ????????????????????????
     *
     * @return
     * @throws BusinessException
     */
    @Override
    public List<PlatformChannelDTO> queryPlatformChannel() throws BusinessException {
        List<PlatformChannel> platformChannels = platformChannelMapper.selectList(null);
        List<PlatformChannelDTO> platformChannelDTOS = PlatformChannelConvert.INSTANCE.listentity2listdto(platformChannels);
        return platformChannelDTOS;
    }

    /**
     * ??? app ????????????????????????
     *
     * @param appId
     * @param platformChannelCodes
     * @throws BusinessException
     */
    @Override
    @Transactional
    public void bindPlatformChannelForApp(String appId, String platformChannelCodes) throws BusinessException {
        // ?????? appId ????????????????????? code ?????? app_platform_channel
        AppPlatformChannel appPlatformChannel = appPlatformChannelMapper.selectOne(new QueryWrapper<AppPlatformChannel>()
                .lambda()
                .eq(AppPlatformChannel::getAppId, appId)
                .eq(AppPlatformChannel::getPlatformChannel, platformChannelCodes));
        // ?????????????????????
        if (appPlatformChannel == null) {
            appPlatformChannel = new AppPlatformChannel();
            appPlatformChannel.setAppId(appId);
            appPlatformChannel.setPlatformChannel(platformChannelCodes);
            appPlatformChannelMapper.insert(appPlatformChannel);
        }
    }

    /**
     * ???????????????????????????????????????????????????
     *
     * @param appId
     * @param platformChannel
     * @return ??????????????? 1??? ???????????? 0
     * @throws BusinessException
     */
    @Override
    public int queryAppBindPlatformChannel(String appId, String platformChannel) throws BusinessException {
        int count = appPlatformChannelMapper.selectCount(
                new QueryWrapper<AppPlatformChannel>().lambda().eq(AppPlatformChannel::getAppId, appId)
                    .eq(AppPlatformChannel::getPlatformChannel, platformChannel)
        );
        return count > 0 ? 1 : 0;
    }

    @Override
    public List<PayChannelDTO> queryPayChannelByPlatformChannel(String platformChannelCode) {
        return platformChannelMapper.selectPayChannelByPlatformChannel(platformChannelCode);
    }

    /**
     * ????????????????????????
     *
     * @param payChannelParamDTO
     * @throws BusinessException
     */
    @Override
    public void savePayChannelParam(PayChannelParamDTO payChannelParamDTO) throws BusinessException {
        if (payChannelParamDTO == null || StringUtils.isBlank(payChannelParamDTO.getAppId()) ||
            StringUtils.isBlank(payChannelParamDTO.getPlatformChannelCode()) ||
            StringUtils.isBlank(payChannelParamDTO.getPayChannel())) {
            throw new BusinessException(CommonErrorCode.E_300009);
        }
        // ??????appId ????????????????????????????????????????????????id
        Long appPlatformChannelId = selectIdByAppPlatformChannel(payChannelParamDTO.getAppId(), payChannelParamDTO.getPlatformChannelCode());
        if (appPlatformChannelId == null) {
            // ???????????????????????????????????????????????????????????????
            throw new BusinessException(CommonErrorCode.E_300010);
        }
        // ????????????????????????????????? id ?????????????????????????????????
        PayChannelParam payChannelParam = payChannelParamMapper.selectOne(new LambdaQueryWrapper<PayChannelParam>()
                .eq(PayChannelParam::getAppPlatformChannelId, appPlatformChannelId)
                .eq(PayChannelParam::getPayChannel, payChannelParamDTO.getPayChannel()));
        // ??????????????????
        if (payChannelParam != null) {
            payChannelParam.setChannelName(payChannelParamDTO.getChannelName());
            payChannelParam.setParam(payChannelParamDTO.getParam());
            payChannelParamMapper.updateById(payChannelParam);
        } else {
            // ???????????????
            PayChannelParam entity = PayChannelParamConvert.INSTANCE.dto2entity(payChannelParamDTO);
            entity.setId(null);
            // ??????????????????????????? id
            entity.setAppPlatformChannelId(appPlatformChannelId);
            payChannelParamMapper.insert(entity);
        }

        // ????????? redis
        updateCache(payChannelParamDTO.getAppId(), payChannelParamDTO.getPlatformChannelCode());
    }

    private void updateCache(String appId, String platformChannel) {
        // ?????? redis ??????
        // 1. key ?????????
        String redisKey = RedisUtil.keyBuilder(appId, platformChannel);
        // 2. ?????? redis, ?????? key ????????????
        Boolean exists = cache.exists(redisKey);
        if (exists) {   // ??????????????????
            cache.del(redisKey);
        }
        // 3. ??????????????????????????????????????????????????????????????????????????????????????????
        Long appPlatformChannelId = selectIdByAppPlatformChannel(appId, platformChannel);
        if (appPlatformChannelId != null) {
            // ?????? appPlatformChannelId ??? pay_channel_param ????????????????????????
            List<PayChannelParam> payChannelParams = payChannelParamMapper.selectList(new LambdaQueryWrapper<PayChannelParam>()
                    .eq(PayChannelParam::getAppPlatformChannelId, appPlatformChannelId));
            List<PayChannelParamDTO> paramDTOS = PayChannelParamConvert.INSTANCE.listentity2listdto(payChannelParams);
            cache.set(redisKey, JSON.toJSONString(paramDTOS).toString());
        }
    }

    /**
     * ?????????????????????????????????????????????????????????????????????????????????
     *
     * @param appId
     * @param platformChannel
     * @return
     * @throws BusinessException
     */
    @Override
    public List<PayChannelParamDTO> queryPayChannelParamByAppAndPlatform(String appId, String platformChannel) throws BusinessException {
        // ???????????????
        // 1. key ?????????
        String redisKey = RedisUtil.keyBuilder(appId, platformChannel);
        Boolean exists = cache.exists(redisKey);
        if (exists) {
            String value = cache.get(redisKey);
            return JSONObject.parseArray(value, PayChannelParamDTO.class);
        }
        // ???????????? id ???????????????????????? app_platform_channel ??????
        Long appPlatformChannelId = selectIdByAppPlatformChannel(appId, platformChannel);
        if (appPlatformChannelId == null) {
            return null;
        }
        // ?????? appPlatformChannelId ??? pay_channel_param ????????????????????????
        List<PayChannelParam> payChannelParams = payChannelParamMapper.selectList(new LambdaQueryWrapper<PayChannelParam>()
                .eq(PayChannelParam::getAppPlatformChannelId, appPlatformChannelId));
        List<PayChannelParamDTO> paramDTOS = PayChannelParamConvert.INSTANCE.listentity2listdto(payChannelParams);
        // ????????????
        updateCache(appId, platformChannel);
        return paramDTOS;
    }

    /**
     * ???????????????????????????????????????????????????????????????????????????
     *
     * @param appId
     * @param platformChannel
     * @param payChannel
     * @return
     * @throws BusinessException
     */
    @Override
    public PayChannelParamDTO queryParamByAppPlatformAndPayChannel(String appId, String platformChannel, String payChannel) throws BusinessException {
        List<PayChannelParamDTO> payChannelParamDTOS = queryPayChannelParamByAppAndPlatform(appId, platformChannel);
        return payChannelParamDTOS.stream()
                .filter(payChannelParamDTO -> payChannelParamDTO.getPayChannel().equals(payChannel))
                .findFirst()
                .get();
    }

    /**
     * ?????? appId ???????????????????????????????????????????????? id
     * @param appId
     * @param platformChannelCode
     * @return
     */
    private Long selectIdByAppPlatformChannel(String appId, String platformChannelCode) {
        AppPlatformChannel appPlatformChannel = appPlatformChannelMapper.selectOne(new LambdaQueryWrapper<AppPlatformChannel>()
                .eq(AppPlatformChannel::getAppId, appId)
                .eq(AppPlatformChannel::getPlatformChannel, platformChannelCode));
        if (appPlatformChannel != null) {
            return appPlatformChannel.getId();
        }
        return null;
    }
}
