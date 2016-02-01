package com.neoteric.starter.request;

import java.util.Objects;

public class RequestOperator implements RequestObject {

    private final OperatorType operator;

    private RequestOperator(OperatorType operator) {
        this.operator = operator;
    }

    public static RequestOperator of(String operator) {
        return of(OperatorType.fromString(operator));
    }

    public static RequestOperator of(OperatorType operatorType) {
        return new RequestOperator(operatorType);
    }

    public OperatorType getOperator() {
        return operator;
    }

    @Override
    public RequestObjectType getType() {
        return RequestObjectType.OPERATOR;
    }

    @Override
    public String toString() {
        return "<" + operator + ">";
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof RequestOperator)) {
            return false;
        }
        RequestOperator other = (RequestOperator) obj;
        return Objects.equals(this.operator, other.operator);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(operator);
    }
}
