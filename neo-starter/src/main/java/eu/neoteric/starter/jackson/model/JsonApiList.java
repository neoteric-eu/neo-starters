package eu.neoteric.starter.jackson.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class JsonApiList<T> {

    public static final String DATA_JSON = "data";
    public static final String META_JSON = "meta";
    public static final String META_TOTAL_JSON = "total";

    @JsonProperty(DATA_JSON)
    private final List<T> data;

    @JsonProperty(META_JSON)
    private final Map<String, Object> meta;

    private final int hashCodeValue;

    @JsonCreator
    public JsonApiList(@JsonProperty(DATA_JSON) List<T> data, @JsonProperty(META_JSON) Map<String, Object> meta) {
        this.data = data == null ? ImmutableList.of() : ImmutableList.copyOf(data);
        this.meta = meta == null ? ImmutableMap.of() : ImmutableMap.copyOf(meta);
        this.hashCodeValue = Objects.hash(data, meta);
    }

    public List<T> getData() {
        return data;
    }

    public Map<String, Object> getMeta() {
        return meta;
    }

    @JsonIgnore
    public int getTotal() {
        return meta.containsKey(META_TOTAL_JSON) ? (int) meta.get(META_TOTAL_JSON) : 0;
    }

    @Override
    public int hashCode() {
        return hashCodeValue;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("data", data)
                .add("meta", meta)
                .toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof JsonApiList) {
            JsonApiList<?> other = (JsonApiList<?>) obj;
            return Objects.equals(this.meta, other.meta) &&
                    Objects.deepEquals(this.data, other.data);
        } else {
            return false;
        }
    }

    public static <T> JsonApiListBuilder<T> wrap(T singleElementData) {
        return wrap(Lists.newArrayList(singleElementData));
    }

    public static <T> JsonApiListBuilder<T> wrap(List<T> data) {
        return new JsonApiListBuilder<T>(data);
    }

    public static class JsonApiListBuilder<T> {
        private List<T> data;
        private Map<String, Object> meta;

        public JsonApiListBuilder() {
        }

        public JsonApiListBuilder(List<T> data) {
            this.data = data;
        }

        public JsonApiListBuilder<T> meta(Map<String, Object> meta) {
            this.meta = meta;
            return this;
        }

        public JsonApiListBuilder<T> meta(String field, Object value) {
            if (meta == null) {
                meta = Maps.newHashMap();
            }
            meta.put(field, value);
            return this;
        }

        public JsonApiListBuilder<T> total(int total) {
            if (meta == null) {
                meta = Maps.newHashMap();
            }
            meta.put(JsonApiList.META_TOTAL_JSON, total);
            return this;
        }

        public JsonApiList<T> build() {
            return new JsonApiList<T>(data, meta);
        }
    }
}