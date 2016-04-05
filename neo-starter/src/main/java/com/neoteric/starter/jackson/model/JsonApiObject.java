package com.neoteric.starter.jackson.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonApiObject<T> {

    public static final String DATA_JSON = "data";
    public static final String META_JSON = "meta";

    @JsonProperty(DATA_JSON)
    private final T data;

    @JsonProperty(META_JSON)
    private final Map<String, Object> meta;

    private final int hashCodeValue;

    @JsonCreator
    public JsonApiObject(@JsonProperty(DATA_JSON) T data, @JsonProperty(META_JSON) Map<String, Object> meta) {
        this.data = data;
        this.meta = meta == null ? ImmutableMap.of() : ImmutableMap.copyOf(meta);
        this.hashCodeValue = Objects.hash(data, meta);
    }

    public T getData() {
        return data;
    }

    public Map<String, Object> getMeta() {
        return meta;
    }

    @Override
    public int hashCode() {
        return hashCodeValue;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof JsonApiObject) {
            JsonApiObject<?> other = (JsonApiObject<?>) obj;
            return Objects.equals(this.meta, other.meta) &&
                    Objects.deepEquals(this.data, other.data);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("data", data)
                .add("meta", meta)
                .toString();
    }

    public static <T> JsonApiObjectBuilder<T> wrap(T object) {
        return new JsonApiObjectBuilder<T>(object);
    }

    public static class JsonApiObjectBuilder<T> {
        private T objectToWrap;
        private Map<String, Object> meta;

        public JsonApiObjectBuilder() {
        }

        public JsonApiObjectBuilder(T objectToWrap) {
            this.objectToWrap = objectToWrap;
        }

        public JsonApiObjectBuilder<T> meta(Map<String, Object> meta) {
            this.meta = meta;
            return this;
        }

        public JsonApiObjectBuilder<T> meta(String metaKey, Object metaValue) {
            if (meta == null) {
                meta = Maps.newHashMap();
            }
            meta.put(metaKey, metaValue);
            return this;
        }

        public JsonApiObject<T> build() {
            return new JsonApiObject<T>(objectToWrap, meta);
        }
    }

}