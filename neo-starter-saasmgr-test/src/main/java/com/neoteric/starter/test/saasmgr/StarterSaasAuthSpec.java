package com.neoteric.starter.test.saasmgr;

import com.jayway.restassured.spi.AuthFilter;

public final class StarterSaasAuthSpec {

    public final static AuthFilter ANY_TOKEN_AUTH = (requestSpec, responseSpec, ctx) -> {
        requestSpec
                .header("X-Customer-Id", "customerId")
                .header("Authorization", "token abc");
        return ctx.next(requestSpec, responseSpec);
    };
}