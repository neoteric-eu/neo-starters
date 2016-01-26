package com.neoteric.starter.request;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RequestParametersTest {

    @Test
    public void shouldCreateEmptyRequestParameters() {
        RequestParameters requestParameters = RequestParameters.builder().build();
        assertThat(requestParameters.getFilters()).isEmpty();
        assertThat(requestParameters.getSort()).isEmpty();
        assertThat(requestParameters.getFirst()).isZero();
        assertThat(requestParameters.getPageSize()).isZero();
    }
}
