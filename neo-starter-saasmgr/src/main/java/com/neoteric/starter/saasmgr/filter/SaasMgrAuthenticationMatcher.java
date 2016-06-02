package com.neoteric.starter.saasmgr.filter;

import com.neoteric.starter.saasmgr.client.feign.SaasMgr;
import com.neoteric.starter.utils.PrefixResolver;
import lombok.AllArgsConstructor;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

@AllArgsConstructor
public class SaasMgrAuthenticationMatcher implements RequestMatcher {

    private final String applicationPath;

    @Override
    public boolean matches(HttpServletRequest request) {
        return new NegatedRequestMatcher(
                new AndRequestMatcher(
                        ContainsSaasMgrHeadersMatcher.INSTANCE,
                        new AntPathRequestMatcher(applicationPath + "/**")))
                .matches(request);
    }

    private enum ContainsSaasMgrHeadersMatcher implements RequestMatcher {
        INSTANCE;

        @Override
        public boolean matches(HttpServletRequest request) {
            return !(StringUtils.isEmpty(request.getHeader(SaasMgr.AUTHORIZATION_HEADER)) ||
                    StringUtils.isEmpty(request.getHeader(SaasMgr.CUSTOMER_ID_HEADER)));
        }
    }
}
