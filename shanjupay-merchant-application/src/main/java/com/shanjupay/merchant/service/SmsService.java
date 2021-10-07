package com.shanjupay.merchant.service;

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
}
