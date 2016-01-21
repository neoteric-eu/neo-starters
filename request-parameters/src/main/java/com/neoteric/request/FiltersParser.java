package com.neoteric.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class FiltersParser {
    private static final Logger LOG = LoggerFactory.getLogger(FiltersParser.class);

    private FiltersParser() {
        // Prevents instantiation of the class.
    }

    public static Map<RequestObject, Object> parseFilters(Map<String, Object> rawFilters) {
        Map<RequestObject, Object> requestParameters = new HashMap<>();

        rawFilters.forEach((key, entry) -> {
//            LOG.warn("Key: {}, Entry: {}", key, entry);
            if (isNotFieldNorLogicalOperator(key)) {
                throw new IllegalStateException("isNotFieldNorLogicalOperator");
            }
            if (isField(key)) {
                if (!(entry instanceof Map)) {
                    throw new IllegalStateException("Bad value type for field: " + entry.getClass().toString());
                }
                requestParameters.put(RequestField.of(key), processFieldValue((Map) entry));
            } else {
                if (!(entry instanceof Map)) {
                    throw new IllegalStateException("Bad value type for or operation: " + entry.getClass().toString());
                }
                requestParameters.put(RequestLogicalOperator.of(key), processRootLogicalOperatorValue((Map) entry));
            }
        });
        LOG.error("Parsed map: {}", requestParameters);
        return requestParameters;
    }

    private static Map<RequestObject, Object> processRootLogicalOperatorValue(Map<String, Object> orEntry) {
        Map<RequestObject, Object> orEntryMap = new HashMap<>();
        orEntry.forEach((key, entry) -> {
//            LOG.warn("Processing Root Or operator: Key: {}, Entry: {}", key, entry);
            if (!isField(key)) {
                throw new IllegalStateException("Or in root node can't be applied with other operators");
            }
            if (!(entry instanceof Map)) {
                throw new IllegalStateException("Bad value type for field: " + entry.getClass().toString());
            }
            orEntryMap.put(RequestField.of(key), processFieldValue((Map) entry));
        });
        return orEntryMap;
    }

    private static Map<RequestObject, Object> processLogicalOperatorValue(Map<String, Object> orEntry) {
        Map<RequestObject, Object> orEntryMap = new HashMap<>();
        orEntry.forEach((key, entry) -> {
//            LOG.warn("Processing Or operator: Key: {}, Entry: {}", key, entry);
            if (!isOperator(key)) {
                throw new IllegalStateException("Nested Or operator can't be applied with fields");
            }
            RequestOperator operator = RequestOperator.of(key);
            ValueType valueType = operator.getOperator().getValueType();
            if (valueType.equals(ValueType.ARRAY) && !(entry instanceof List)) {
                throw new IllegalStateException("Bad value type for operator: " + entry.getClass().toString());
            }
            orEntryMap.put(RequestOperator.of(key), entry);
        });
        return orEntryMap;
    }

    private static Map<RequestObject, Object> processFieldValue(Map<String, Object> fieldEntry) {
        Map<RequestObject, Object> fieldEntryMap = new HashMap<>();
        fieldEntry.forEach((key, entry) -> {
//            LOG.warn("Processing Field: Key: {}, Entry: {}", key, entry);
            if (isNotOperatorNorLogicalOperator(key)) {
                throw new IllegalStateException("isNotOperatorNorLogicalOperator");
            }
            if (isLogicalOperator(key)) {
                if (!(entry instanceof Map)) {
                    throw new IllegalStateException("Bad value type for logical operator: " + entry.getClass().toString());
                }
                fieldEntryMap.put(RequestLogicalOperator.of(key), processLogicalOperatorValue((Map) entry));
            } else {
                RequestOperator operator = RequestOperator.of(key);
                ValueType valueType = operator.getOperator().getValueType();
                if (valueType.equals(ValueType.ARRAY) && !(entry instanceof List)) {
                    throw new IllegalStateException("Bad value type for operator: " + entry.getClass().toString());
                }
                fieldEntryMap.put(RequestOperator.of(key), entry);
            }
        });
        return fieldEntryMap;
    }

    private static boolean isLogicalOperator(String key) {
        return LogicalOperatorType.contains(key);
    }

    private static boolean isOperator(String key) {
        return OperatorType.contains(key);
    }

    private static boolean isField(String key) {
        return !key.startsWith("$");
    }

    private static boolean isNotOperatorNorLogicalOperator(String key) {
        return !(isLogicalOperator(key) || isOperator(key));
    }

    private static boolean isNotFieldNorLogicalOperator(String key) {
        return !(!key.startsWith("$") || LogicalOperatorType.contains(key));
    }
}