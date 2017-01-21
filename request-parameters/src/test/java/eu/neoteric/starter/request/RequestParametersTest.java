package eu.neoteric.starter.request;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RequestParametersTest {

    @Test
    public void shouldCreateEmptyRequestParameters() {
        RequestParameters requestParameters = RequestParameters.builder().build();
        Assertions.assertThat(requestParameters.getFilters()).isEmpty();
        Assertions.assertThat(requestParameters.getSort()).isEmpty();
        assertThat(requestParameters.getFirst()).isZero();
        assertThat(requestParameters.getPageSize()).isZero();
    }
}
