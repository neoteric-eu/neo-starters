package com.neoteric.starter.request;

import java.util.Objects;

public class RequestLogicalOperator implements RequestObject {

    private final LogicalOperatorType logicalOperator;

    private RequestLogicalOperator(LogicalOperatorType logicalOperator) {
        this.logicalOperator = logicalOperator;
    }

    public static RequestLogicalOperator of(String logicalOperator) {
        return of(LogicalOperatorType.fromString(logicalOperator));
    }

    public static RequestLogicalOperator of(LogicalOperatorType logicalOperatorType) {
        return new RequestLogicalOperator(logicalOperatorType);
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
        return "<" + logicalOperator + ">";
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof RequestLogicalOperator)) {
            return false;
        }
        RequestLogicalOperator other = (RequestLogicalOperator) obj;
        return Objects.equals(this.logicalOperator, other.logicalOperator);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(logicalOperator);
    }
}
