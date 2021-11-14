package com.shanjupay.uaa.api;

import java.util.Map;

public interface OauthClientDetailsService {
    public void createClientDetails(Map map);

    /**
     * 根据appId查询 client信息 appId也就是client_Id
     * @param appId
     * @return
     */
    public Map getClientDetailsByClientId(String appId);
}
