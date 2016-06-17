package com.neoteric.starter.jackson.model;

import com.neoteric.starter.jersey.jackson.model.JsonApiObject;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class JsonApiObjectTest {

    @Test
    public void createEmptyObjectTest() throws Exception {
        JsonApiObject<String> jsonApiObject = JsonApiObject.wrap((String)null).build();
        assertThat(jsonApiObject.getData()).isNull();
        assertThat(jsonApiObject.getMeta()).isEmpty();
    }

    @Test
    public void shouldCreateJsonApiObjectWithMetaTest() throws Exception {
        JsonApiObject<String> jsonApiObject = JsonApiObject.wrap("ABC").meta("key", "value").build();
        assertThat(jsonApiObject.getData()).isEqualTo("ABC");
        assertThat(jsonApiObject.getMeta()).hasSize(1).containsEntry("key", "value");
    }
}