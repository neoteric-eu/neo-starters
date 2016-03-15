package com.neoteric.starter.saasmgr.client;

import com.neoteric.starter.saasmgr.client.model.LoginData;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.netflix.feign.FeignClient;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import static com.neoteric.starter.saasmgr.SaasMgrStarterConstants.SAAS_MGR_AUTH_CACHE;
import static com.neoteric.starter.saasmgr.SaasMgrStarterConstants.SAAS_MGR_CACHE_MANAGER;

@FeignClient("saasManager")
public interface SaasMgrClient {

    String AUTHORIZATION_HEADER = "Authorization";
    String CUSTOMER_ID_HEADER = "X-Customer-Id";

    @GET
    @Path("api/v1/users/authInfo")
    @Consumes(MediaType.APPLICATION_JSON)
    @Cacheable(cacheManager = SAAS_MGR_CACHE_MANAGER, cacheNames = SAAS_MGR_AUTH_CACHE)
    LoginData getLoginInfo(@HeaderParam(AUTHORIZATION_HEADER) String token, @HeaderParam(CUSTOMER_ID_HEADER) String customerId);
}
