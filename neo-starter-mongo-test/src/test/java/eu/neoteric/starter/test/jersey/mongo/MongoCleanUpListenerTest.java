package eu.neoteric.starter.test.jersey.mongo;


import com.mongodb.BasicDBObject;
import eu.neoteric.starter.mongo.MongoConvertersAutoConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@RunWith(SpringRunner.class)
@TestPropertySource(properties = "spring.data.mongodb.port=0")
@ContextConfiguration(classes = {MongoConvertersAutoConfiguration.class,
        EmbeddedMongoAutoConfiguration.class,
        MongoAutoConfiguration.class,
        MongoDataAutoConfiguration.class})
public class MongoCleanUpListenerTest {

    public static final BasicDBObject ADDITIONAL_INDEX = new BasicDBObject("someIndexedField", 1);
    public static final BasicDBObject DEFAULT_INDEX = new BasicDBObject("_id", 1);
    public static final String COLLECTION_NAME = "collection";

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    ApplicationContext applicationContext;

    private MongoCleanUpListener cleanUpListener = new MongoCleanUpListener();

    private TestContext mockTestClassContext(Object instance) {
        TestContext testContext = mock(TestContext.class);
        given(testContext.getTestClass()).willReturn((Class) instance.getClass());
        given(testContext.getApplicationContext()).willReturn(applicationContext);
        return testContext;
    }

    @Before
    public void setCollection() {
        mongoTemplate.getCollection(COLLECTION_NAME).insert(new BasicDBObject());
        mongoTemplate.getCollection(COLLECTION_NAME).createIndex(ADDITIONAL_INDEX);
    }

    @Test
    public void shouldClearCollectionAndLeaveIndex() throws Exception {

        WithClearCollectionClassAnnotation instance = new WithClearCollectionClassAnnotation();
        cleanUpListener.afterTestMethod(mockTestClassContext(instance));

        assertThat(mongoTemplate.getCollection(COLLECTION_NAME).count()).isEqualTo(0);
        assertThat(mongoTemplate.getCollection(COLLECTION_NAME).getIndexInfo().size()).isEqualTo(2);
        assertThat(mongoTemplate.getCollection(COLLECTION_NAME).getIndexInfo().get(0).get("key")).isEqualTo(DEFAULT_INDEX);
        assertThat(mongoTemplate.getCollection(COLLECTION_NAME).getIndexInfo().get(1).get("key")).isEqualTo(ADDITIONAL_INDEX);
    }

    @Test
    public void shouldDropCollectionAndIndex() throws Exception {
        WithClearDropCollectionClassAnnotation instance = new WithClearDropCollectionClassAnnotation();
        cleanUpListener.afterTestMethod(mockTestClassContext(instance));
        assertThat(mongoTemplate.getCollection(COLLECTION_NAME).count()).isEqualTo(0);
        assertThat(mongoTemplate.getCollection(COLLECTION_NAME).getIndexInfo().size()).isEqualTo(0);
    }

    @ClearCollections(COLLECTION_NAME)
    static class WithClearCollectionClassAnnotation {
    }

    @ClearCollections(value = COLLECTION_NAME, drop = true)
    static class WithClearDropCollectionClassAnnotation {
    }
}