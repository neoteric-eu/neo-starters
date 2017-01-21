package eu.neoteric.starter.request;

import java.util.HashMap;
import java.util.Map;

public enum LogicalOperatorType {
    OR("$or");

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
        if (logicalOperatorType == null) {
            throw new IllegalArgumentException(String.format("Unsupported type %s.", operator));
        }
        return logicalOperatorType;
    }

    public static boolean contains(String operator) {
        return Holder.MAP.containsKey(operator);
    }
}