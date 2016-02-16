package com.neoteric.starter.auth.saasmgr;

import com.neoteric.starter.auth.saasmgr.client.SaasMgrClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

public enum ContainsSaasMgrHeadersMatcher implements RequestMatcher {
    INSTANCE;

    private static final Logger LOG = LoggerFactory.getLogger(ContainsSaasMgrHeadersMatcher.class);

    @Override
    public boolean matches(HttpServletRequest request) {
        LOG.error("ABC: {}", request.getPathInfo());
        return !StringUtils.isEmpty(request.getHeader(SaasMgrClient.AUTHORIZATION_HEADER)) &&
                !StringUtils.isEmpty(request.getHeader(SaasMgrClient.CUSTOMER_ID_HEADER));
    }
}
