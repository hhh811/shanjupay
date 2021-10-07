package com.shanjupay.merchant.api;

import com.shanjupay.merchant.api.dto.MerchantDTO;

public interface MerchantService {

    MerchantDTO queryMerchantById(Long merchantId);

    /**
     * 商户注册
     * @param merchantDTO
     * @return
     */
    MerchantDTO createMerchant(MerchantDTO merchantDTO);
}
