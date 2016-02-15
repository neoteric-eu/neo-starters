package com.neoteric.starter.auth.saasmgr.client;

import com.neoteric.starter.auth.saasmgr.client.model.LoginData;
import org.springframework.cloud.netflix.feign.FeignClient;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

@FeignClient(serviceId = "saasManager")
public interface SaasMgrClient {

    String AUTHORIZATION_HEADER = "Authorization";
    String CUSTOMER_ID_HEADER = "X-Customer-Id";

    @GET
    @Path("api/v1/users/authInfo")
    @Consumes(MediaType.APPLICATION_JSON)
    LoginData getLoginInfo(@HeaderParam(AUTHORIZATION_HEADER) String token, @HeaderParam(CUSTOMER_ID_HEADER) String customerId);
}
