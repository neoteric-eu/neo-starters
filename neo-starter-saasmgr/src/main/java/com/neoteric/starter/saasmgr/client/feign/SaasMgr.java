package com.neoteric.starter.saasmgr.client.feign;

import com.neoteric.starter.saasmgr.client.SaasMgrClient;
import com.neoteric.starter.saasmgr.model.LoginData;
import com.neoteric.starter.saasmgr.model.LoginDataWrapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static com.neoteric.starter.saasmgr.SaasMgrStarterConstants.SAAS_MGR_AUTH_CACHE;
import static com.neoteric.starter.saasmgr.SaasMgrStarterConstants.SAAS_MGR_CACHE_MANAGER;

@FeignClient("${neostarter.saasmgr.feign.name:saasManager}")
public interface SaasMgr {

    String AUTHORIZATION_HEADER = "Authorization";
    String CUSTOMER_ID_HEADER = "X-Customer-Id";

    @RequestMapping(method = RequestMethod.GET, value = "/api/v2/users/authInfo",  produces = MediaType.APPLICATION_JSON_VALUE)
    @Cacheable(cacheManager = SAAS_MGR_CACHE_MANAGER, cacheNames = SAAS_MGR_AUTH_CACHE)
    LoginDataWrapper getLoginInfo(@RequestHeader(AUTHORIZATION_HEADER) String token, @RequestHeader(CUSTOMER_ID_HEADER) String customerId);
}
