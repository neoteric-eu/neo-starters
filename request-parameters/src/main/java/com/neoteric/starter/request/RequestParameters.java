package com.neoteric.starter.request;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.neoteric.starter.request.sort.RequestSort;

import java.util.List;
import java.util.Map;

public class RequestParameters {

    private final Map<RequestObject, Object> filters;
    private final List<RequestSort> sort;
    private final int first;
    private final int pageSize;

    public static Builder builder() {
        return new Builder();
    }

    public Map<RequestObject, Object> getFilters() {
        return filters;
    }

    public List<RequestSort> getSort() {
        return sort;
    }

    public int getFirst() {
        return first;
    }

    public int getPageSize() {
        return pageSize;
    }

    private RequestParameters(Builder builder) {
        this.filters = builder.filters == null ? ImmutableMap.of() : ImmutableMap.copyOf(builder.filters);
        this.sort = builder.sort == null ? ImmutableList.of() : ImmutableList.copyOf(builder.sort);
        this.first = builder.first;
        this.pageSize = builder.pageSize;
    }

    public static class Builder {
        private Map<RequestObject, Object> filters;
        private List<RequestSort> sort;
        private int first;
        private int pageSize;

        public Builder setFilters(Map<RequestObject, Object> filters) {
            this.filters = filters;
            return this;
        }

        public Builder setSort(List<RequestSort> sort) {
            this.sort = sort;
            return this;
        }

        public Builder setFirst(int first) {
            this.first = first;
            return this;
        }

        public Builder setPageSize(int pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public RequestParameters build() {
            return new RequestParameters(this);
        }
    }
}
