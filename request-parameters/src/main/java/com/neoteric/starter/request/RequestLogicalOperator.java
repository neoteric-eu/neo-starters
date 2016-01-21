package com.neoteric.starter.request;

public class RequestLogicalOperator implements RequestObject {

    private final LogicalOperatorType logicalOperator;

    private RequestLogicalOperator(LogicalOperatorType logicalOperator) {
        this.logicalOperator = logicalOperator;
    }

    public static RequestLogicalOperator of(String logicalOperator) {
        return new RequestLogicalOperator(LogicalOperatorType.fromString(logicalOperator));
    }

    public LogicalOperatorType getOperator() {
        return logicalOperator;
    }

    @Override
    public RequestObjectType getType() {
        return RequestObjectType.LOGICAL_OPERATOR;
    }

    @Override
    public String toString() {
        return "[" + logicalOperator + "]";
    }
}
