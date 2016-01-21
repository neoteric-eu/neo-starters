package com.neoteric.starter.request;

public class RequestOperator implements RequestObject {

    private final OperatorType operator;

    private RequestOperator(OperatorType operator) {
        this.operator = operator;
    }

    public static RequestOperator of(String operator) {
        return new RequestOperator(OperatorType.fromString(operator));
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
        return "[" + operator + "]";
    }
}
