package com.neoteric.starter.jersey.http.feign;

import com.neoteric.starter.jersey.StarterConstants;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.MDC;

public class RequestIdAppendInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header(StarterConstants.REQUEST_ID, MDC.get(StarterConstants.REQUEST_ID));
    }
}