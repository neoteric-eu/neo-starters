package com.neoteric.starter.request.tracing;

import com.neoteric.starter.StarterConstants;
import com.neoteric.starter.utils.PrefixResolver;
import lombok.extern.slf4j.Slf4j;
import org.jboss.logging.MDC;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Servlet filter to pick up ID of the com.neoteric.starter.request. If not found, generates new one and propagates.
 * Available in MDC by REQUEST_ID_HEADER key
 */
@WebFilter
@Slf4j
public class RequestIdFilter extends OncePerRequestFilter {

    private final String applicationPath;
    private final RequestIdGenerator idGenerator;
    private final List<RequestIdListener> requestIdListeners;

    public RequestIdFilter(RequestIdGenerator idGenerator, List<RequestIdListener> requestIdListeners, String applicationPath) {
        this.idGenerator = idGenerator;
        this.requestIdListeners = requestIdListeners;
        this.applicationPath = applicationPath == null ? "" : applicationPath;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = new UrlPathHelper().getPathWithinApplication(request);
        return !path.startsWith(applicationPath);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestId = getRequestId(request);
        requestIdListeners.forEach(requestIdListener -> requestIdListener.afterRequestIdSet(requestId, request, response));
        // populates the attribute
        MDC.put(StarterConstants.REQUEST_ID_HEADER, requestId);
        response.addHeader(StarterConstants.REQUEST_ID_HEADER, requestId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            LOG.trace("Removing Request ID from MDC");
            MDC.remove(StarterConstants.REQUEST_ID_HEADER);
            requestIdListeners.forEach(requestIdListener -> requestIdListener.onCleanUp(requestId, request, response));
        }
    }

    private String getRequestId(HttpServletRequest request) {
        String requestId = request.getHeader(StarterConstants.REQUEST_ID_HEADER);
        if (StringUtils.isEmpty(requestId)) {
            requestId = idGenerator.generateId();
            LOG.trace("Request ID header not found. Assigning new Request ID: [{}]", requestId);
        } else {
            LOG.trace("Request ID header found: [{}].", requestId);
        }
        return requestId;
    }

}