package com.neoteric.starter.mongo;


import com.github.mongobee.changeset.ChangeLog;
import com.github.mongobee.changeset.ChangeSet;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.junit.After;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import static org.assertj.core.api.Assertions.assertThat;

public class MongoBeeAutoConfigurationTest extends AbstractMongoDBTest {

    private AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

    @Test
    public void shouldAddChangeSetWithDefaults() throws Exception {
        EnvironmentTestUtils.addEnvironment(this.context, mongoUri());
        registerAndRefresh(PackageConfiguration.class,
                MongoAutoConfiguration.class,
                MongoDataAutoConfiguration.class,
                MongoBeeAutoConfiguration.class);

        MongoTemplate mongoTemplate = context.getBean(MongoTemplate.class);
        assertThat(mongoTemplate.getCollection("mycollection").findOne()).isNotNull();
    }

    @ChangeLog
    public static class SomeChange {
        @ChangeSet(order = "001", id = "someChangeWithJongo", author = "testAuthor")
        public void someChange(Jongo jongo) {
            // type: org.jongo.Jongo : Jongo driver can be used, used for simpler notation
            // example:
            MongoCollection mycollection = jongo.getCollection("mycollection");
            mycollection.insert("{test : 1}");
        }
    }

    @AutoConfigurationPackage
    @Configuration
    static class PackageConfiguration {
    }

    @After
    public void closeContext() {
        if (this.context != null) {
            this.context.close();
        }
    }

    private void registerAndRefresh() {
        this.context.refresh();
    }

    private void registerAndRefresh(Class<?>... annotatedClasses) {
        this.context.register(annotatedClasses);
        registerAndRefresh();
    }

    private String mongoUri() {
        return String.join("", "spring.data.mongodb.uri=mongodb://localhost:", String.valueOf(freePort), "/test");
    }
}