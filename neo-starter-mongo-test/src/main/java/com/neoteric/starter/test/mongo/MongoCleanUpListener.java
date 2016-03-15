package com.neoteric.starter.test.mongo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.core.Ordered;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import java.util.Arrays;

public class MongoCleanUpListener extends AbstractTestExecutionListener {

    private static final Logger LOG = LoggerFactory.getLogger(MongoCleanUpListener.class);

    @Override
    public void afterTestMethod(TestContext testContext) throws Exception {
        DropCollections annotation = testContext.getTestClass().getAnnotation(DropCollections.class);
        if (annotation == null) {
            return;
        }
        MongoTemplate mongoTemplate;
        try {
            mongoTemplate = testContext.getApplicationContext().getBean("mongoTemplate", MongoTemplate.class);
        } catch (NoSuchBeanDefinitionException e) {
            LOG.warn("mongoTemplate bean not found. Skipping collections cleanup.", e);
            return;
        }
        Arrays.stream(annotation.value()).forEach(mongoTemplate::dropCollection);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
