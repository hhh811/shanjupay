package com.shanjupay.merchant.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.common.util.PhoneUtil;
import com.shanjupay.merchant.api.MerchantService;
import com.shanjupay.merchant.api.dto.MerchantDTO;
import com.shanjupay.merchant.convert.MerchantConvert;
import com.shanjupay.merchant.entity.Merchant;
import com.shanjupay.merchant.mapper.MerchantMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MerchantServiceImpl implements MerchantService {

    @Autowired
    MerchantMapper merchantMapper;

    /**
     * 根据ID查询详细信息
     *
     * @param merchantId
     * @return
     * @throws
     */
    @Override
    public MerchantDTO queryMerchantById(Long merchantId) {
        Merchant merchant = merchantMapper.selectById(merchantId);
        if (merchant == null) {
            return null;
        }
        MerchantDTO merchantDTO = MerchantConvert.INSTANCE.entity2dto(merchant);
        return merchantDTO;
    }

    /**
     * 商户注册
     *
     * @param merchantDTO
     * @return
     */
    @Override
    @Transactional
    public MerchantDTO createMerchant(MerchantDTO merchantDTO) throws BusinessException {

        // 校验
        if (merchantDTO == null) {
            throw new BusinessException(CommonErrorCode.E_100108);  // 传入对象为空
        }
        if (StringUtils.isBlank(merchantDTO.getMobile())) {
            throw new BusinessException(CommonErrorCode.E_100112);  // 手机号为空
        }
        if (!PhoneUtil.isMatches(merchantDTO.getMobile())) {
            throw new BusinessException(CommonErrorCode.E_100109);  // 手机号格式不合法
        }
        if (StringUtils.isBlank(merchantDTO.getUserName())) {
            throw new BusinessException(CommonErrorCode.E_100110);  // 用户名为空
        }
        if (StringUtils.isBlank(merchantDTO.getPassword())) {
            throw new BusinessException(CommonErrorCode.E_100111);  // 密码为空
        }
        // 手机号唯一性
        LambdaQueryWrapper<Merchant> lambdaQueryWrapper = new LambdaQueryWrapper<Merchant>()
                .eq(Merchant::getMobile, merchantDTO.getMobile());
        Integer count = merchantMapper.selectCount(lambdaQueryWrapper);
        if (count > 0) {
            throw new BusinessException(CommonErrorCode.E_100113);  // 手机号已存在
        }
        // DTO 转换成 entity, 用 mapstruct
        Merchant entity = MerchantConvert.INSTANCE.dto2entity(merchantDTO);
        // 设置审核状态 0-未申请
        entity.setAuditStatus("0");
        // TODO 其他系统保存相关信息

        // 保存商户信息到商户表, 插入过程中会生成id
        merchantMapper.insert(entity);

        // 再将 entity 转成 dto, 插入过程会增加 id
        MerchantDTO merchantDTONew = MerchantConvert.INSTANCE.entity2dto(entity);
        return merchantDTONew;
    }

    /**
     * 资质申请
     *
     * @param merchantId  商户 id
     * @param merchantDTO 资质申请信息
     * @throws BusinessException
     */
    @Override
    @Transactional
    public void applyMerchant(Long merchantId, MerchantDTO merchantDTO) throws BusinessException {
        // 接受资质申请信息，更新到商户列表
        if (merchantDTO == null || merchantId == null) {
            throw new BusinessException(CommonErrorCode.E_100108);
        }
        // 根据 id 查询商户
        Merchant merchant = merchantMapper.selectById(merchantId);
        if (merchant == null) {
            throw new BusinessException(CommonErrorCode.E_200002);
        }
        Merchant merchant_update = MerchantConvert.INSTANCE.dto2entity(merchantDTO);
        merchant_update.setAuditStatus("1");    // 已申请，待审核
        merchant_update.setTenantId(merchant.getTenantId());    // 租户 id
        // 更新
        merchantMapper.updateById(merchant_update);
    }
}
