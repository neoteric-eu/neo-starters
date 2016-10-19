package com.neoteric.starter.quartz;

import com.google.common.collect.Maps;
import com.mongodb.MongoClientURI;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties("neostarter.quartz")
@Getter
@Setter
@SuppressWarnings("PMD.ImmutableField")
public class QuartzProperties {

    /**
     *  If disabled, all triggers are going to be vetoed.
     */
    private boolean enabled = true;

    /**
     * If enabled Quartz will always use RAMJobStore.
     */
    private boolean forceRamJobStore;

    /**
     * Additional properties to add to Quartz.
     */
    private Map<String, String> properties = new HashMap<>();
    private QuartzMongo mongo = new QuartzMongo();
    private QuartzJdbc jdbc = new QuartzJdbc();

    @Getter
    @Setter
    public static class QuartzJdbc {

        /**
         * Quartz DDL SQL file location.
         */
        private String schema = "classpath:db/schema-quartz.sql";

        /**
         * Should Quartz DDL SQL file be run at startup.
         */
        private boolean initialize = true;
    }

    @Getter
    @Setter
    public static class QuartzMongo {

        /**
         * If provided,  will be used as prefix for all Quartz collections.
         */
        private String collectionPrefix;

        /**
         * If provided, will be used for Quartz Mongo connection. If not spring.data.mongodb.uri will be used instead.
         */
        private String uri;

        public Map<String, String> buildFromMongoProperties(MongoProperties mongoProps) {
            Map<String, String> props = Maps.newHashMap();
            props.put("org.quartz.jobStore.class", "com.novemberain.quartz.mongodb.MongoDBJobStore");
            props.put("org.quartz.threadPool.threadCount", "1");
            if (uri == null) {
                Assert.notNull(mongoProps.getUri());
                uri = mongoProps.getUri();
            }
            String database = new MongoClientURI(uri).getDatabase();
            Assert.notNull(database);
            props.put("org.quartz.jobStore.dbName", database);


            if (!StringUtils.isEmpty(collectionPrefix)) {
                props.put("org.quartz.jobStore.collectionPrefix", collectionPrefix);
            }
            props.put("org.quartz.jobStore.mongoUri", uri);
            return props;
        }
    }
}