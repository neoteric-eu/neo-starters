package com.neoteric.starter.saasmgr.filter;

import com.neoteric.starter.saasmgr.client.feign.SaasMgr;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

public enum ContainsSaasMgrHeadersMatcher implements RequestMatcher {
    INSTANCE;

    @Override
    public boolean matches(HttpServletRequest request) {
        return !(StringUtils.isEmpty(request.getHeader(SaasMgr.AUTHORIZATION_HEADER)) ||
                StringUtils.isEmpty(request.getHeader(SaasMgr.CUSTOMER_ID_HEADER)));
    }
}
