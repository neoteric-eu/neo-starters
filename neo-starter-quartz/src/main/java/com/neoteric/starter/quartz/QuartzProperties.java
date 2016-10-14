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
import java.util.Optional;

@ConfigurationProperties("neostarter.quartz")
@Getter
@Setter
public class QuartzProperties {

    private boolean enabled = true;
    private boolean forceRamJobStore = false;
    private Map<String, String> properties = new HashMap<>();
    private QuartzMongo mongo = new QuartzMongo();
    private QuartzJdbc jdbc = new QuartzJdbc();

    @Getter
    @Setter
    public static class QuartzJdbc {
        private String schema = "classpath:db/schema-quartz.sql";
        private boolean initialize = true;
    }

    @Getter
    @Setter
    public static class QuartzMongo {
        private String collectionPrefix;
        private String uri;
        private String username;
        private char[] password;

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

            if (username == null) {
                username = mongoProps.getUsername();
                password = mongoProps.getPassword();
            }

            if (!StringUtils.isEmpty(collectionPrefix)) {
                props.put("org.quartz.jobStore.collectionPrefix", collectionPrefix);
            }

            if (!StringUtils.isEmpty(username)) {
                props.put("org.quartz.jobStore.username", username);
                props.put("org.quartz.jobStore.password", String.valueOf(password));

            }
            props.put("org.quartz.jobStore.mongoUri", uri);
            return props;
        }
    }
}