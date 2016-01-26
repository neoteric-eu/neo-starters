package com.neoteric.starter.mongo.request;


import java.util.Map;

public final class FieldMapper {

    private final Map<String, String> remapping;

    private FieldMapper(Map<String, String> remapping) {
        this.remapping = remapping;
    }

    public static FieldMapper of(Map<String, String> remapping) {
        return new FieldMapper(remapping);
    }

    public String get(String initialField) {
        return remapping.getOrDefault(initialField, initialField);
    }
}
