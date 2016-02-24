package com.neoteric.starter.jackson.model;

import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public class JsonApiListBuilder<T> {

    private List<T> data;
    private Map<String, Object> meta;

    public JsonApiListBuilder(List<T> data) {
        this.data = data;
    }

    public JsonApiListBuilder<T> setMeta(Map<String, Object> meta) {
        this.meta = meta;
        return this;
    }

    public JsonApiListBuilder<T> addMeta(String field, Object value) {
        if (meta == null) {
            meta = Maps.newHashMap();
        }
        meta.put(field, value);
        return this;
    }

    public JsonApiListBuilder<T> addTotal(int total) {
        if (meta == null) {
            meta = Maps.newHashMap();
        }
        meta.put(JsonApiList.META_TOTAL_JSON, total);
        return this;
    }

    public JsonApiList<T> get() {
        return new JsonApiList<T>(data, meta);
    }
}
