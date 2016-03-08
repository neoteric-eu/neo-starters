package com.neoteric.starter.mongo.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.test.EnvironmentTestUtils;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import java.util.Arrays;

public class MongoCleanUpListener extends AbstractTestExecutionListener {

    private static final Logger LOG = LoggerFactory.getLogger(MongoCleanUpListener.class);

    @Override
    public void beforeTestClass(TestContext testContext) throws Exception {
        EmbeddedMongoTest annotation = testContext.getTestClass().getAnnotation(EmbeddedMongoTest.class);
        if (annotation == null || annotation.dropCollections().length == 0) {
            return;
        }
        ConfigurableEnvironment environment = (ConfigurableEnvironment)testContext.getApplicationContext().getEnvironment();
        EnvironmentTestUtils.addEnvironment(environment, "spring.data.mongodb.host=localhost", "spring.data.mongodb.port=0");
    }

    @Override
    public void afterTestMethod(TestContext testContext) throws Exception {
        EmbeddedMongoTest annotation = testContext.getTestClass().getAnnotation(EmbeddedMongoTest.class);
        if (annotation == null || annotation.dropCollections().length == 0) {
            return;
        }
        MongoTemplate mongoTemplate;
        try {
            mongoTemplate = testContext.getApplicationContext().getBean("mongoTemplate", MongoTemplate.class);
        } catch (NoSuchBeanDefinitionException e) {
            LOG.warn("mongoTemplate bean not found. Skipping collections cleanup.", e);
            return;
        }
        Arrays.stream(annotation.dropCollections()).forEach(mongoTemplate::dropCollection);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
