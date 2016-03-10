package com.neoteric.starter.auth.saasmgr.test;

import com.jayway.restassured.spi.AuthFilter;

public class RestAssuredAuthSpec {

    public final static AuthFilter ANY_TOKEN_AUTH = (requestSpec, responseSpec, ctx) -> {
        requestSpec
                .header("X-Customer-Id", "customerId")
                .header("Authorization", "token abc");
        return ctx.next(requestSpec, responseSpec);
    };
}