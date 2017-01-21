package eu.neoteric.starter.test.jersey.mongo;

import com.mongodb.BasicDBObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import java.util.Arrays;
import java.util.function.Consumer;

public class MongoCleanUpListener extends AbstractTestExecutionListener {

    private static final Logger LOG = LoggerFactory.getLogger(MongoCleanUpListener.class);

    @Override
    public void afterTestMethod(TestContext testContext) throws Exception {
        ClearCollections annotation = AnnotationUtils.findAnnotation(testContext.getTestClass(), ClearCollections.class);

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

        Consumer<String> consumer = annotation.drop() ? new DropConsumer(mongoTemplate) : new ClearConsumer(mongoTemplate);
        Arrays.stream(annotation.value()).forEach(consumer);
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }


    private final class DropConsumer implements Consumer<String> {
        private final MongoTemplate mongoTemplate;

        DropConsumer(MongoTemplate mongoTemplate) {
            this.mongoTemplate = mongoTemplate;
        }

        @Override
        public void accept(String collection) {
            mongoTemplate.getCollection(collection).drop();
        }
    }

    private final class ClearConsumer implements Consumer<String> {
        private final MongoTemplate mongoTemplate;

        ClearConsumer(MongoTemplate mongoTemplate) {
            this.mongoTemplate = mongoTemplate;
        }

        @Override
        public void accept(String collection) {
            mongoTemplate.getCollection(collection).remove(new BasicDBObject());
        }
    }
}
