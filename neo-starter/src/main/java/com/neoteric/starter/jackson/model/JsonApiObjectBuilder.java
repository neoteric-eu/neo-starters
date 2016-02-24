package com.neoteric.starter.jackson.model;

import com.google.common.collect.Maps;

import java.util.Map;

public class JsonApiObjectBuilder<T> {

    private T objectToWrap;
    private Map<String, Object> meta;

    public JsonApiObjectBuilder(T objectToWrap) {
        this.objectToWrap = objectToWrap;
    }

    public JsonApiObjectBuilder<T> setMeta(Map<String, Object> meta) {
        this.meta = meta;
        return this;
    }

    public JsonApiObjectBuilder<T> addMeta(String metaKey, Object metaValue) {
        if (meta == null) {
            meta = Maps.newHashMap();
        }
        meta.put(metaKey, metaValue);
        return this;
    }

    public JsonApiObject<T> get() {
        return new JsonApiObject<T>(objectToWrap, meta);
    }
}
