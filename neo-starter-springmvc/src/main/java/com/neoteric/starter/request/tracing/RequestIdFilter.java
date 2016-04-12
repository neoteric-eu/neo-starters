package com.neoteric.starter.request.tracing;

import com.neoteric.starter.StarterConstants;
import org.jboss.logging.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * Servlet filter to pick up ID of the com.neoteric.starter.request. If not found, generates new one and propagates.
 * Available in MDC by REQUEST_ID key
 */
@WebFilter
public class RequestIdFilter extends OncePerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(RequestIdFilter.class);
    private final String applicationPath;

    public RequestIdFilter(String applicationPath) {
        this.applicationPath = applicationPath;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
//        String path = new UrlPathHelper().getPathWithinApplication(request);
//        return !path.startsWith(applicationPath);
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestId = request.getHeader(StarterConstants.REQUEST_ID);

        String path = new UrlPathHelper().getPathWithinApplication(request);
        if (requestId == null || requestId.isEmpty()) {
            requestId = UUID.randomUUID().toString();
            LOG.trace("Request ID header not found. Assigning new Request ID: [{}]", requestId);
        } else {
            LOG.trace("Request ID header found: [{}].", requestId);
        }
        MDC.put(StarterConstants.REQUEST_ID, requestId);
        response.setHeader(StarterConstants.REQUEST_ID, requestId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(StarterConstants.REQUEST_ID);
        }
    }
}