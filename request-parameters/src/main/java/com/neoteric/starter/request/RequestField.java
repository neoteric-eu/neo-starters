package com.neoteric.starter.request;

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
        return "<" + fieldName + ">";
    }


    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RequestField)) {
            return false;
        }
        RequestField other = (RequestField) o;
        return Objects.equals(this.fieldName, other.fieldName);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(fieldName);
    }
}
