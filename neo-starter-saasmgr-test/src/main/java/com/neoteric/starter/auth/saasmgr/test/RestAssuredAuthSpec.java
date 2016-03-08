package com.neoteric.starter.auth.saasmgr.test;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.specification.RequestSpecification;

public class RestAssuredAuthSpec {

    public final static RequestSpecification ANY_TOKEN_AUTH = new RequestSpecBuilder()
            .addHeader("X-Customer-Id", "customerId")
            .addHeader("Authorization", "token abc")
            .build();
}