package com.neoteric.starter.auth;


import com.neoteric.starter.auth.basics.LoginInfo;
import org.springframework.cloud.netflix.feign.FeignClient;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@FeignClient(serviceId = "saasClient")
public interface SaasClient {

    @GET
    @Path("api/v1/users/authInfo")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    LoginInfo getLoginInfo(@HeaderParam("Authorization") String token, @HeaderParam("X-Customer-Id") String customerId);
}
