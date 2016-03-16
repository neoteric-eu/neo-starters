package com.neoteric.starter.jackson.model;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class JsonApiListTest {

    @Test
    public void createEmptyListTest() throws Exception {
        JsonApiList<String> jsonApiList = JsonApiList.<String>wrap(null).build();
        assertThat(jsonApiList.getData()).isEmpty();
        assertThat(jsonApiList.getMeta()).isEmpty();
    }

    @Test
    public void shouldCreateJsonApiListWithMetaTest() throws Exception {
        JsonApiList<String> jsonApiList = JsonApiList.<String>wrap("ABC").meta("key", "value").build();
        assertThat(jsonApiList.getData()).hasSize(1).contains("ABC");
        assertThat(jsonApiList.getMeta()).hasSize(1).containsEntry("key", "value");
    }
}
