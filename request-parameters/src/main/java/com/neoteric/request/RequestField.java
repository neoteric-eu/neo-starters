package com.neoteric.request;

import java.util.Objects;

public class RequestField implements RequestObject {

    private final String fieldName;

    private RequestField(String fieldName) {
        this.fieldName = fieldName;
    }

    public static RequestField of(String fieldName) {
        return new RequestField(fieldName);
    }

    public String getFieldName() {
        return fieldName;
    }

    @Override
    public RequestObjectType getType() {
        return RequestObjectType.FIELD;
    }

    @Override
    public String toString() {
        return "[" + fieldName + "]";
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestField that = (RequestField) o;
        return Objects.equals(fieldName, that.fieldName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fieldName);
    }
}
