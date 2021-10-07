package com.shanjupay.merchant.service;

import com.shanjupay.merchant.api.dto.MerchantDTO;

/**
 * <p>
 *     手机短信服务
 * </p>
 */
public interface SmsService {

    /**
     * 获取手机验证码
     * @param phone
     * @return
     */
    String sendMsg(String phone);

    /**
     * 校验验证码，抛出异常则校验无效
     * @param verifyKey 验证码key
     * @param verifyCode 验证码
     */
    void checkVerifyCode(String verifyKey, String verifyCode);
}
