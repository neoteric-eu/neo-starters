package com.neoteric.starter.test.jersey.mongo;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "models")
public class TestModel {
    private final String name;

    public TestModel(String name) {
        this.name = name;
    }
}