package com.neoteric.starter.saasmgr.client;

import com.neoteric.starter.saasmgr.model.LoginData;
import lombok.AllArgsConstructor;
import org.springframework.web.client.RestTemplate;

@AllArgsConstructor
public class RestTemplateSaasMgrClient implements SaasMgrClient {

    private final RestTemplate restTemplate;

    @Override
    public LoginData getLoginInfo(String token, String customerId) {
        return null;
    }
}
