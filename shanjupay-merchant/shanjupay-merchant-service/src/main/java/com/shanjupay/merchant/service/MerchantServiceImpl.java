package com.shanjupay.merchant.service;

import com.shanjupay.merchant.api.MerchantService;
import com.shanjupay.merchant.api.dto.MerchantDTO;
import com.shanjupay.merchant.entity.Merchant;
import com.shanjupay.merchant.mapper.MerchantMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

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
}
