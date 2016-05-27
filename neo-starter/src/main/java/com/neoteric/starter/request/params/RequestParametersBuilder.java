package com.neoteric.starter.request.params;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.neoteric.starter.request.FiltersParser;
import com.neoteric.starter.request.RequestObject;
import com.neoteric.starter.request.RequestParameters;
import com.neoteric.starter.request.sort.RequestSort;
import com.neoteric.starter.request.sort.SortParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public final class RequestParametersBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(RequestParametersBuilder.class);
    private static final String FIRST_PARAM = "first";
    private static final String PAGE_SIZE_PARAM = "pageSize";
    private static final String FILTERS_PARAM = "filters";
    private static final String SORT_PARAM = "sort";
    private static final Map<RequestObject, Object> EMPTY_FILTERS = ImmutableMap.of();
    private static final List<RequestSort> EMPTY_SORT = ImmutableList.of();

    private RequestParametersBuilder() {
        // Prevents instantiation of this class
    }

    public static RequestParameters buildFrom(HttpServletRequest request, ObjectMapper requestMapper) throws ServletRequestBindingException {
        LOG.trace("Request Query string: {}", request.getQueryString());
        int first = ServletRequestUtils.getIntParameter(request, FIRST_PARAM, 0);
        int pageSize = ServletRequestUtils.getIntParameter(request, PAGE_SIZE_PARAM, 0);
        String filters = ServletRequestUtils.getStringParameter(request, FILTERS_PARAM);
        String sort = ServletRequestUtils.getStringParameter(request, SORT_PARAM);

        return RequestParameters.builder()
                .setFirst(first)
                .setPageSize(pageSize)
                .setFilters(processRequestFilters(filters, requestMapper))
                .setSort(processRequestSort(sort, requestMapper))
                .build();
    }

    private static Map<RequestObject, Object> processRequestFilters(String filters, ObjectMapper requestMapper)
            throws ServletRequestBindingException {
        if (filters == null) {
            return EMPTY_FILTERS;
        }
        Map<String, Object> filtersMap = parseQueryString(filters, requestMapper);
        try {
            return FiltersParser.parseFilters(filtersMap);
        } catch (IllegalStateException e) {
            LOG.error("Error parsing filters: {}", e.getMessage());
            throw new ServletRequestBindingException("Error parsing filters", e);
        }
    }

    private static List<RequestSort> processRequestSort(String sort, ObjectMapper requestMapper)
            throws ServletRequestBindingException {
        if (sort == null) {
            return EMPTY_SORT;
        }
        Map<String, Object> sortMap = parseQueryString(sort, requestMapper);
        try {
            return SortParser.parseSort(sortMap);
        } catch (IllegalStateException e) {
            LOG.error("Error parsing sort: {}", e.getMessage());
            throw new ServletRequestBindingException("Error parsing sort", e);
        }
    }

    private static Map<String, Object> parseQueryString(String queryString, ObjectMapper requestMapper)
            throws ServletRequestBindingException {
        try {
            return requestMapper.readValue(queryString, new TypeReference<Map<String, Object>>() {
            });
        } catch (IOException e) {
            LOG.error("Error parsing query filters/sort parameter: {}", e.getMessage());
            throw new ServletRequestBindingException("Error parsing query filters/sort parameter", e);
        }
    }
}
