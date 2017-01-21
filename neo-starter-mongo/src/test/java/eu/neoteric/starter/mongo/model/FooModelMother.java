package eu.neoteric.starter.mongo.model;

import com.google.common.collect.Lists;

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

    public static FooModel withTags(String name, String... tags) {
        return FooModel.newBuilder()
                .setName(name)
                .setTags(Lists.newArrayList(tags))
                .build();
    }

}
