package com.neoteric.starter.saasmgr.client;

import com.neoteric.starter.saasmgr.model.LoginData;

public interface SaasMgrClient {
    LoginData getLoginInfo(String token, String customerId);
}
