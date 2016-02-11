package com.neoteric.starter.auth;


import com.neoteric.starter.auth.basics.LoginInfo;
import org.springframework.cloud.netflix.feign.FeignClient;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

@FeignClient(serviceId = "saasClient")
public interface SaasClient {

    @GET
    @Path("api/v1/users/authInfo")
    @Consumes(MediaType.APPLICATION_JSON)
    LoginInfo getLoginInfo(@HeaderParam("Authorization") String token, @HeaderParam("X-Customer-Id") String customerId);
}
