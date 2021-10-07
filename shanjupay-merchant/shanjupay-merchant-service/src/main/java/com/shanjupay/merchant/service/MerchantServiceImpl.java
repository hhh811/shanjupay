package com.shanjupay.merchant.service;

import com.shanjupay.merchant.api.MerchantService;
import com.shanjupay.merchant.api.dto.MerchantDTO;
import com.shanjupay.merchant.entity.Merchant;
import com.shanjupay.merchant.mapper.MerchantMapper;
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
        MerchantDTO merchantDTO = new MerchantDTO();
        merchantDTO.setId(merchant.getId());
        merchantDTO.setMerchantName(merchant.getMerchantName());
        merchantDTO.setMerchantNo(Long.valueOf(merchant.getMerchantNo()));
        merchantDTO.setMerchantAddress(merchant.getMerchantAddress());
        merchantDTO.setMerchantType(merchant.getMerchantType());
        merchantDTO.setBusinessLicensesImg(merchant.getBusinessLicenseImg());
        merchantDTO.setIdCardFrontImg(merchant.getIdCardFrontImg());
        merchantDTO.setIdCardAfterImg(merchant.getIdCardAfterImg());
        merchantDTO.setUsername(merchant.getUserName());
        merchantDTO.setMobile(merchant.getMobile());
        merchantDTO.setContactAddress(merchant.getContactAddress());
        merchantDTO.setAuditStatus(merchant.getAuditStatus());
        merchantDTO.setTenantId(merchant.getTenantId());
        //TODO password,  use converter
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
    public MerchantDTO createMerchant(MerchantDTO merchantDTO) {
        Merchant merchant = new Merchant();
        // 设置审核状态 0-未申请
        merchant.setAuditStatus("0");
        merchant.setMobile(merchantDTO.getMobile());
        merchant.setUserName(merchantDTO.getUsername());
        // TODO 其他系统保存相关信息

        // 保存商户信息到商户表, 插入过程中会生成id
        merchantMapper.insert(merchant);

        //将新增商户的id返回
        merchantDTO.setId(merchant.getId());
        return merchantDTO;
    }
}
