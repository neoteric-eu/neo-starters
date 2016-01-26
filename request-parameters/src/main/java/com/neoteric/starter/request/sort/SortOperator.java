package com.neoteric.starter.request.sort;

import java.util.HashMap;
import java.util.Map;

public enum SortOperator {

    ORDER("$order");

    private String value;

    SortOperator(String value) {
        this.value = value;
        Holder.MAP.put(value, this);
    }

    public String getValue() {
        return value;
    }

    public static boolean contains(String name) {
        return Holder.MAP.containsKey(name);
    }

    private static class Holder {
        static Map<String, SortOperator> MAP = new HashMap<>();
    }

    public static SortOperator fromString(String name) {
        SortOperator operatorType = Holder.MAP.get(name.toLowerCase());
        if (operatorType == null) {
            throw new IllegalArgumentException(String.format("Unsupported type %s.", name));
        }
        return operatorType;
    }

}
