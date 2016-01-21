package com.neoteric.starter.request.params;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neoteric.starter.request.FiltersParser;
import com.neoteric.starter.request.RequestObject;
import com.neoteric.starter.request.RequestParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public final class RequestParametersBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(RequestParametersBuilder.class);
    private static final String FIRST_PARAM = "first";
    private static final String PAGE_SIZE_PARAM = "pageSize";
    private static final String FILTERS_PARAM = "filters";

    private RequestParametersBuilder() {
        // Prevents instantiation of this class
    }

    public static RequestParameters buildFrom(HttpServletRequest request, ObjectMapper requestMapper) throws ServletRequestBindingException {
        LOG.debug("Request Query string: {}", request.getQueryString());
        int first = ServletRequestUtils.getIntParameter(request, FIRST_PARAM, 0);
        int pageSize = ServletRequestUtils.getIntParameter(request, PAGE_SIZE_PARAM, 0);
        String filters = ServletRequestUtils.getStringParameter(request, FILTERS_PARAM);

        RequestParameters.Builder requestParametersBuilder = RequestParameters.builder()
                .setFirst(first)
                .setPageSize(pageSize);

        Optional<Map<RequestObject, Object>> requestObjectsMap = processRequestFilters(filters, requestMapper);
        if (requestObjectsMap.isPresent()) {
            requestParametersBuilder.setFilters(requestObjectsMap.get());
        }
        return requestParametersBuilder.build();
    }

    private static Optional<Map<RequestObject, Object>> processRequestFilters(String filters, ObjectMapper requestMapper)
            throws ServletRequestBindingException {
        if (filters == null) {
            return Optional.empty();
        }
        Map<String, Object> filtersMap = parseQueryString(filters, requestMapper);
        try {
            return Optional.of(FiltersParser.parseFilters(filtersMap));
        } catch (IllegalStateException e) {
            LOG.error("Error parsing filters: {}", e.getMessage());
            throw new ServletRequestBindingException("Error parsing filters", e);
        }
    }

    private static Map<String, Object> parseQueryString(String filters, ObjectMapper requestMapper)
            throws ServletRequestBindingException {
        try {
            return requestMapper.readValue(filters, new TypeReference<Map<String, Object>>() {
            });
        } catch (IOException e) {
            LOG.error("Error parsing query filters parameter: {}", e.getMessage());
            throw new ServletRequestBindingException("Error parsing query filters parameter", e);
        }
    }
}
