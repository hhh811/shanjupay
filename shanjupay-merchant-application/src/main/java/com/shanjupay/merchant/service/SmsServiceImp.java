package com.shanjupay.merchant.service;

import com.alibaba.fastjson.JSON;
import com.shanjupay.merchant.api.dto.MerchantDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class SmsServiceImp implements SmsService{

    @Value("${sms.url}")
    private String smsUrl;

    @Value("${sms.effectiveTime}")
    private String effectiveTime;

    @Autowired
    RestTemplate restTemplate;

    /**
     * 获取手机验证码
     * @param phone
     * @return
     */
    @Override
    public String sendMsg(String phone) {
        String url = smsUrl + "/generate?name=sms&effectiveTime=" + effectiveTime;
        log.info("调用短信微服务发送验证码: url: {}", url);

        // 请求体
        Map<String, Object> body = new HashMap<>();
        body.put("mobile", phone);
        // 请求头
        HttpHeaders httpHeaders = new HttpHeaders();
        // 设置数据格式为json
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        // 封装请求参数
        HttpEntity entity = new HttpEntity(body, httpHeaders);

        Map responseMap = null;

        try {
            // post请求
            ResponseEntity<Map> exchange = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
            log.info("调用短信微服务发送验证码: 返回值: {}", JSON.toJSONString(exchange));
            // 获取响应
            responseMap = exchange.getBody();
        } catch (Exception e) {
            log.info(e.getMessage(), e);
            throw new RuntimeException("发送验证码出错");
        }
        // 取出body中的result数据
        if (responseMap == null || responseMap.get("result") == null) {
            throw new RuntimeException("发送验证码出错");
        }
        Map resultMap = (Map) responseMap.get("result");
        return resultMap.get("key").toString();
    }

    /**
     * 校验验证码，抛出异常则校验无效
     *
     * @param verifyKey  验证码key
     * @param verifyCode 验证码
     */
    @Override
    public void checkVerifyCode(String verifyKey, String verifyCode) {
        String url = smsUrl + "/verify?name=sms&verificationCode=" + verifyCode + "&verificationKey=" + verifyKey;
        Map responseMap = null;
        try {
            // 请求验证校验码
            ResponseEntity<Map> exchange = restTemplate.exchange(url, HttpMethod.POST, HttpEntity.EMPTY, Map.class);
            responseMap = exchange.getBody();
            log.info("校验验证码, 相应内容: {}", JSON.toJSONString(responseMap));
        } catch (Exception e) {
            e.printStackTrace();
            log.info(e.getMessage(), e);
            throw new RuntimeException("验证码错误");
        }

        if (responseMap == null || responseMap.get("result") == null || !(Boolean) responseMap.get("result")) {
            throw new RuntimeException("验证码错误");
        }
    }

}
