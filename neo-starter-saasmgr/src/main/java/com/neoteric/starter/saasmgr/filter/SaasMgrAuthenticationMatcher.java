package com.neoteric.starter.saasmgr.filter;

import com.neoteric.starter.saasmgr.client.feign.SaasMgr;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

import static com.neoteric.starter.saasmgr.SaasMgrStarterConstants.LOG_PREFIX;

@Slf4j
@AllArgsConstructor
public class SaasMgrAuthenticationMatcher implements RequestMatcher {

	@Override
	public boolean matches(HttpServletRequest request) {
		boolean matches = StringUtils.hasLength(request.getHeader(SaasMgr.AUTHORIZATION_HEADER))
				            && StringUtils.hasLength(request.getHeader(SaasMgr.CUSTOMER_ID_HEADER));
		if (matches) {
			LOG.trace("{}Authorization and X-Customer-Id headers found", LOG_PREFIX);
		}
		else {
			LOG.trace("{}Authorization and X-Customer-Id headers not found", LOG_PREFIX);
		}
		return matches;
	}
}