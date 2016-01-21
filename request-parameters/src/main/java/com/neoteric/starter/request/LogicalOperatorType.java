package com.neoteric.starter.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public enum LogicalOperatorType {
    OR("$or");

    private static final Logger LOG = LoggerFactory.getLogger(LogicalOperatorType.class);

    private String value;

    private static class Holder {
        static Map<String, LogicalOperatorType> MAP = new HashMap<>();
    }

    LogicalOperatorType(String value) {
        this.value = value;
        Holder.MAP.put(value, this);
    }

    public String get() {
        return value;
    }

    public static LogicalOperatorType fromString(String operator) {
        LogicalOperatorType logicalOperatorType = Holder.MAP.get(operator);
        if(logicalOperatorType == null) {
            throw new IllegalStateException(String.format("Unsupported type %s.", operator));
        }
        return logicalOperatorType;
    }

    public static boolean contains(String operator) {
        return Holder.MAP.containsKey(operator);
    }
}