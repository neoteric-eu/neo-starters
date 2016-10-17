package com.neoteric.starter.quartz;

import com.neoteric.starter.quartz.db.QuartzDatabaseInitializer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;

import static com.neoteric.starter.quartz.StarterQuartzConstants.LOG_PREFIX;

@Configuration
@ConditionalOnBean(DataSource.class)
@ConditionalOnMissingClass("com.novemberain.quartz.mongodb.MongoDBJobStore")
@ConditionalOnProperty(prefix = "neostarter.quartz", name = "forceRamJobStore", matchIfMissing = true, havingValue = "false")
class QuartzDataSourceConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public QuartzDatabaseInitializer quartzDatabaseInitializer(DataSource dataSource,
                                                               ResourceLoader resourceLoader,
                                                               QuartzProperties quartzProperties) {
        return new QuartzDatabaseInitializer(dataSource, resourceLoader, quartzProperties);
    }

    @Bean
    public SchedulerFactoryBeanCustomizer dataSourceCustomizer(DataSource dataSource,
                                                               // workaround to force initializer to populate data
                                                               // before SchedulerFactoryBean runs afterPropertiesSet method
                                                               QuartzDatabaseInitializer initializer) {
        return new QuartzDatasourceCustomizer(dataSource);
    }

    @Slf4j
    @AllArgsConstructor
    @Order(1)
    private static final class QuartzDatasourceCustomizer implements SchedulerFactoryBeanCustomizer {

        private final DataSource dataSource;

        @Override
        public void customize(SchedulerFactoryBean bean) {
            LOG.info("{}Registering Quartz datasource", LOG_PREFIX);
            bean.setDataSource(dataSource);
        }
    }
}
