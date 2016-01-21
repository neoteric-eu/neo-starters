package com.neoteric.starter.http.feign;

import com.neoteric.starter.Constants;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.MDC;

public class RequestIdAppendInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header(Constants.REQUEST_ID, MDC.get(Constants.REQUEST_ID));
    }
}