package eu.neoteric.starter.quartz;

import com.novemberain.quartz.mongodb.MongoDBJobStore;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.Map;

import static eu.neoteric.starter.quartz.StarterQuartzConstants.LOG_PREFIX;

@Configuration
@ConditionalOnClass(MongoDBJobStore.class)
@EnableConfigurationProperties(MongoProperties.class)
@ConditionalOnProperty(prefix = "neostarter.quartz", name = "forceRamJobStore", matchIfMissing = true, havingValue = "false")
class QuartzMongoDbConfiguration {

    @Bean
    SchedulerFactoryBeanCustomizer quartzMongoDbStoreCustomizer(QuartzProperties quartzProperties,
                                                                MongoProperties mongoProperties) {
        return new QuartzMongoDbCustomizer(quartzProperties, mongoProperties);
    }

    @Slf4j
    @AllArgsConstructor
    @Order(1)
    private static final class QuartzMongoDbCustomizer implements SchedulerFactoryBeanCustomizer {

        private final QuartzProperties quartzProperties;
        private final MongoProperties mongoProperties;

        @Override
        public void customize(SchedulerFactoryBean bean) {
            LOG.info("{}Registering Quartz MongoDB defaults", LOG_PREFIX);
        }

        @Override
        public Map<String, String> quartzProperties() {
            return quartzProperties.getMongo().buildFromMongoProperties(mongoProperties);
        }
    }
}
