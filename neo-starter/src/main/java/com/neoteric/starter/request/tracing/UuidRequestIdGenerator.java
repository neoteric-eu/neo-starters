package com.neoteric.starter.request.tracing;

import java.util.UUID;

public class UuidRequestIdGenerator implements RequestIdGenerator {

    @Override
    public String generateId() {
        return UUID.randomUUID().toString();
    }
}
