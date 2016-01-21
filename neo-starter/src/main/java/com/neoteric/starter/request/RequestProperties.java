package com.neoteric.starter.request;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("neostarter.request")
public class RequestProperties {

    public enum FiltersFormat {
        RISON, JSON;
    }

    /**
     * Filters query parameter format. Available are two types: rison / json
     */
    private FiltersFormat filtersFormat = FiltersFormat.JSON;


    public FiltersFormat getFiltersFormat() {
        return filtersFormat;
    }

    public void setFiltersFormat(FiltersFormat filtersFormat) {
        this.filtersFormat = filtersFormat;
    }
}

