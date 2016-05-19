package com.neoteric.starter.utils;

import org.springframework.util.StringUtils;

public final class PrefixResolver {

    private PrefixResolver() {}

    public static String resolve(String initialPrefix) {
        if (!StringUtils.hasLength(initialPrefix)) {
            return initialPrefix;
        }
        String prefixToReturn = initialPrefix;

        if (!initialPrefix.startsWith("/")) {
            prefixToReturn = "/" + initialPrefix;
        }
        if (initialPrefix.endsWith("/")) {
            prefixToReturn = prefixToReturn.substring(0, prefixToReturn.length() - 1);
        }
        return prefixToReturn;
    }
}