package com.neoteric.starter.request.sort;

import java.util.Objects;

public class RequestSort {

    private final String fieldName;
    private final SortType type;

    private RequestSort(String fieldName, SortType type) {
        this.fieldName = fieldName;
        this.type = type;
    }

    public static RequestSort of(String fieldName, String type) {
        return of(fieldName, SortType.fromString(type));
    }

    public static RequestSort of(String fieldName, SortType type) {
        return new RequestSort(fieldName, type);
    }

    public String getFieldName() {
        return fieldName;
    }

    public SortType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "<" + fieldName + ":" + type + ">";
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof RequestSort)) {
            return false;
        }
        RequestSort other = (RequestSort) obj;
        return Objects.equals(this.fieldName, other.fieldName)
                && Objects.equals(this.type, other.type);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(fieldName, type);
    }
}
