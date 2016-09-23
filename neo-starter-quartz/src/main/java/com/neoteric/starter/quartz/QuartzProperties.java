package com.neoteric.starter.quartz;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

@ConfigurationProperties("neostarter.quartz")
@Getter
@Setter
public class QuartzProperties {

    private boolean enabled = true;
    private boolean forceRamJobStore = false;
    private Map<String, String> properties = new HashMap<>();
    private QuartzMongo mongo = new QuartzMongo();

    @Getter
    @Setter
    public static class QuartzMongo {
        private String collectionPrefix;


        public Map<String,String> buildFromMongoProperties(MongoProperties mongoProps) {
            Map<String, String> props = Maps.newHashMap();
            props.put("org.quartz.jobStore.class", "com.novemberain.quartz.mongodb.MongoDBJobStore");
            props.put("org.quartz.threadPool.threadCount", "1");
            props.put("org.quartz.jobStore.dbName", mongoProps.getDatabase());
            if (!StringUtils.isEmpty(collectionPrefix)) {
                props.put("org.quartz.jobStore.collectionPrefix", collectionPrefix);
            }
            String uri = Optional.ofNullable(mongoProps.getUri())
                    .orElseGet(() -> String.join("", "mongodb://",
                            mongoProps.getUsername(), ":",
                            String.valueOf(mongoProps.getPassword()), "@",
                            mongoProps.getHost(), ":",
                            String.valueOf(mongoProps.getPort()), "/", mongoProps.getDatabase()));
            props.put("org.quartz.jobStore.mongoUri", uri);
            return props;
        }
    }
}