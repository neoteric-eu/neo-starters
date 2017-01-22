package eu.neoteric.starter.mongo;


import com.github.mongobee.Mongobee;
import com.github.mongobee.changeset.ChangeLog;
import com.github.mongobee.changeset.ChangeSet;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class MongoBeeAutoConfigurationTest extends AbstractMongoDBTest {

    public static final String MY_COLLECTION = "mycollection";
    private static final String DATABASE_NAME = "test";
    private AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

    @Test
    public void shouldDisableMongobee() throws Exception {
        EnvironmentTestUtils.addEnvironment(this.context, "neostarter.mongobee.enabled=false");
        registerAndRefresh(MongoAutoConfiguration.class,
                MongoDataAutoConfiguration.class,
                MongoBeeAutoConfiguration.class);

        assertThatExceptionOfType(NoSuchBeanDefinitionException.class)
                .isThrownBy(() -> context.getBean(Mongobee.class));
    }

    @Test
    //Using @AutoConfigurationPackage and spring.data.mongodb.uri
    public void shouldAddChangeSetWithDefaultParameters() throws Exception {
        EnvironmentTestUtils.addEnvironment(this.context, mongoUri());
        registerAndRefresh(PackageConfiguration.class,
                MongoAutoConfiguration.class,
                MongoDataAutoConfiguration.class,
                MongoBeeAutoConfiguration.class);

        assertThat(context.getBean(Mongobee.class)).isNotNull();
        assertThat(getMongoBeeCollection().count()).isEqualTo(2L); // Second changeSet in 'custompackage' package
    }

    @Test
    public void shouldUseCustomMongoUri() throws Exception {
        EnvironmentTestUtils.addEnvironment(this.context,
                String.join("", "neostarter.mongobee.uri=mongodb://localhost:", String.valueOf(freePort), "/test"),
                "spring.data.mongodb.uri=mongodb://BAD_localhost:12345/test");
        registerAndRefresh(PackageConfiguration.class,
                MongoAutoConfiguration.class,
                MongoBeeAutoConfiguration.class);

        assertThat(getMongoBeeCollection().count()).isEqualTo(2L);
    }

    @Test
    public void shouldUseCustomPackage() throws Exception {
        EnvironmentTestUtils.addEnvironment(this.context, mongoUri(),
                "neostarter.mongobee.packageToScan=eu.neoteric.starter.mongo.custompackage");
        registerAndRefresh(PackageConfiguration.class,
                MongoAutoConfiguration.class,
                MongoDataAutoConfiguration.class,
                MongoBeeAutoConfiguration.class);

        assertThat(getMongoBeeCollection().count()).isEqualTo(1L);
    }

    @ChangeLog
    public static class SomeChange {
        @ChangeSet(order = "001", id = "someChange", author = "testAuthor")
        public void someChange(Jongo jongo) {
            MongoCollection mycollection = jongo.getCollection(MY_COLLECTION);
            mycollection.insert("{test : 1}");
        }
    }

    @AutoConfigurationPackage
    @Configuration
    static class PackageConfiguration {
    }

    @After
    public void closeContext() {
        getMongoBeeDatabase().drop();
        if (this.context != null) {
            this.context.close();
        }
    }

    private MongoDatabase getMongoBeeDatabase() {
        return ((MongoClient) getMongo()).getDatabase(DATABASE_NAME);
    }


    private com.mongodb.client.MongoCollection getMongoBeeCollection() {
        return getMongoBeeDatabase().getCollection(MY_COLLECTION);
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