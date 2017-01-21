package eu.neoteric.starter.request.sort;

import java.util.HashMap;
import java.util.Map;

public enum SortType {

    ASC("asc"), DESC("desc");

    private String value;

    SortType(String value) {
        this.value = value;
        Holder.MAP.put(value, this);
    }

    private static class Holder {
        static Map<String, SortType> MAP = new HashMap<>();
    }

    public static SortType fromString(String name) {
        SortType sortType = Holder.MAP.get(name.toLowerCase());
        if (sortType == null) {
            throw new IllegalArgumentException(String.format("Unsupported type %s.", name));
        }
        return sortType;
    }
}
