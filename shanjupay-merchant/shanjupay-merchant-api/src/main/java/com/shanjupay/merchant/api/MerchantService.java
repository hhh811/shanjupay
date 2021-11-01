package com.shanjupay.merchant.api;

import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.merchant.api.dto.MerchantDTO;

public interface MerchantService {

    MerchantDTO queryMerchantById(Long merchantId);

    /**
     * 商户注册
     * @param merchantDTO
     * @return
     */
    MerchantDTO createMerchant(MerchantDTO merchantDTO) throws BusinessException;

    /**
     * 资质申请
     * @param merchantId  商户 id
     * @param merchantDTO  资质申请信息
     * @throws BusinessException
     */
    public void applyMerchant(Long merchantId, MerchantDTO merchantDTO) throws BusinessException;
}
