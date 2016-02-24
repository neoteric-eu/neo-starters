package com.neoteric.starter.jackson.model;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class JsonApiListTest {

    @Test
    public void createEmptyListTest() throws Exception {
        JsonApiList<String> jsonApiList = new JsonApiList<>(null, null);
        assertThat(jsonApiList.getData()).isEmpty();
        assertThat(jsonApiList.getMeta()).isEmpty();
    }

    @Test
    public void createEmptyListViaWrapTest() throws Exception {
        JsonApiList<String> jsonApiList = JsonApiList.<String>wrap(null).get();
        assertThat(jsonApiList.getData()).isEmpty();
        assertThat(jsonApiList.getMeta()).isEmpty();
    }

    @Test
    public void shouldCreateJsonApiListWithMetaTest() throws Exception {
        JsonApiList<String> jsonApiList = JsonApiList.wrap("ABC").addMeta("key", "value").get();
        assertThat(jsonApiList.getData()).hasSize(1).contains("ABC");
        assertThat(jsonApiList.getMeta()).hasSize(1).containsEntry("key", "value");
    }
}
