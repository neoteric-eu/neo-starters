package com.neoteric.starter.jackson.model;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class JsonApiObjectTest {

    @Test
    public void createEmptyObjectTest() throws Exception {
        JsonApiObject<String> jsonApiObject = new JsonApiObject<>(null, null);
        assertThat(jsonApiObject.getData()).isNull();
        assertThat(jsonApiObject.getMeta()).isEmpty();
    }

    @Test
    public void createEmptyObjectViaWrapTest() throws Exception {
        JsonApiObject<String> jsonApiObject = JsonApiObject.<String>wrap(null).get();
        assertThat(jsonApiObject.getData()).isNull();
        assertThat(jsonApiObject.getMeta()).isEmpty();
    }

    @Test
    public void shouldCreateJsonApiObjectWithMetaTest() throws Exception {
        JsonApiObject<String> jsonApiObject = JsonApiObject.wrap("ABC").addMeta("key", "value").get();
        assertThat(jsonApiObject.getData()).isEqualTo("ABC");
        assertThat(jsonApiObject.getMeta()).hasSize(1).containsEntry("key", "value");
    }
}