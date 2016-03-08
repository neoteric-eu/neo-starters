package com.neoteric.starter.jackson.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@Value
@Builder
@JsonDeserialize(builder = JsonApiList.JsonApiListBuilder.class)
public class JsonApiList<T> {

    private static final String META_TOTAL = "total";

    @Singular("data") List<T> data;
    @Singular("meta") Map<String, Object> meta;

    @JsonIgnore
    public int getTotal() {
        return meta.containsKey(META_TOTAL) ? (int) meta.get(META_TOTAL) : 0;
    }

    public static class JsonApiListBuilder<T> {

        public JsonApiListBuilder<T> total(int total) {
            meta(META_TOTAL, total);
            return this;
        }
    }
}
