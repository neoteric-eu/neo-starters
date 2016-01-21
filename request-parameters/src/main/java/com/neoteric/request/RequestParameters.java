package com.neoteric.request;

import java.util.Map;

public class RequestParameters {

    private final Map<RequestObject, Object> filters;
    private final int first;
    private final int pageSize;

    public static Builder builder() {
        return new Builder();
    }

    public Map<RequestObject, Object> getFilters() {
        return filters;
    }

    public int getFirst() {
        return first;
    }

    public int getPageSize() {
        return pageSize;
    }

    private RequestParameters(Builder builder) {
        this.filters = builder.filters;
        this.first = builder.first;
        this.pageSize = builder.pageSize;
    }

    public static class Builder {
        private Map<RequestObject, Object> filters;
        private int first;
        private int pageSize;

        public Builder setFilters(Map<RequestObject, Object> filters) {
            this.filters = filters;
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
