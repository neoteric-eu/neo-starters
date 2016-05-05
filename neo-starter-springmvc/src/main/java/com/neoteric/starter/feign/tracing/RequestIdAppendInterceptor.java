package com.neoteric.starter.feign.tracing;

import com.neoteric.starter.StarterConstants;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

public class RequestIdAppendInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        String requestId = MDC.get(StarterConstants.REQUEST_ID_HEADER);
        if (StringUtils.hasLength(requestId)) {
            requestTemplate.header(StarterConstants.REQUEST_ID_HEADER, requestId);
        }
    }
}