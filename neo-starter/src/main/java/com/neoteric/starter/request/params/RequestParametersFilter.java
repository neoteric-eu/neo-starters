package com.neoteric.starter.request.params;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neoteric.request.RequestParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public final class RequestParametersFilter extends OncePerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(RequestParametersFilter.class);
    private final ObjectMapper requestMapper;
    private final String applicationPath;

    public RequestParametersFilter(ObjectMapper requestMapper, String applicationPath) {
        this.requestMapper = requestMapper;
        this.applicationPath = applicationPath;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = new UrlPathHelper().getPathWithinApplication(request);
        return !path.startsWith(applicationPath);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        initHolder(request,  RequestParametersBuilder.buildFrom(request, requestMapper));

        try {
            filterChain.doFilter(request, response);
        } finally {
            resetHolder();
            LOG.debug("Cleared thread-bound request parameters: {}", request);
        }
    }

    private void initHolder(HttpServletRequest request, RequestParameters requestParameters) {
        RequestParametersHolder.set(requestParameters);
        LOG.debug("Bound request parameters to thread: {}", request);
    }

    private void resetHolder() {
        RequestParametersHolder.reset();
    }
}
