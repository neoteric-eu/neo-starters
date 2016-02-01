package com.neoteric.starter.mongo.request;

import com.neoteric.starter.mongo.request.FooModel;

import java.time.ZonedDateTime;

public class FooModelMother {

    public static FooModel fullyPopulated(String name, Integer count) {
        return fullyPopulated(name, count, null);
    }

    public static FooModel fullyPopulated(String name, Integer count, ZonedDateTime date) {
        return FooModel.newBuilder()
                .setName(name)
                .setCount(count)
                .setDate(date)
                .build();
    }
}
