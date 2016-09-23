package com.neoteric.starter.quartz;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.novemberain.quartz.mongodb.MongoDBJobStore;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static com.neoteric.starter.quartz.StarterQuartzConstants.LOG_PREFIX;
import static org.springframework.core.Ordered.LOWEST_PRECEDENCE;

@Slf4j
@Configuration
@ConditionalOnClass({Scheduler.class, SchedulerFactoryBean.class})
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
@AllArgsConstructor
@EnableConfigurationProperties(QuartzProperties.class)
public class QuartzAutoConfiguration implements ApplicationContextAware {

    private final List<SchedulerFactoryBeanCustomizer> schedulerFactoryBeanCustomizers;
    private ApplicationContext applicationContext;

    @PostConstruct
    public void showSummary() throws SchedulerException {
        Scheduler scheduler = this.applicationContext.getBean(Scheduler.class);
        LOG.info("{}Scheduler summary: {}", LOG_PREFIX, scheduler.getMetaData().getSummary());
    }

    @Bean
    @ConditionalOnProperty(prefix = "neostarter.quartz", name = "enabled", havingValue = "false")
    public SchedulerFactoryBean noOpschedulerFactoryBean() {
        LOG.info("{}Quartz disabled", LOG_PREFIX);
        SchedulerFactoryBean bean = new SchedulerFactoryBean();
        bean.setGlobalTriggerListeners(new VetoAllListener());
        return bean;
    }

    @Bean
    @ConditionalOnProperty(prefix = "neostarter.quartz", name = "enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean
    public SchedulerFactoryBean schedulerFactoryBean() {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setJobFactory(
                new AutowiringSpringBeanJobFactory(this.applicationContext.getAutowireCapableBeanFactory()));

        Map<String,String> mergedProperties = Maps.newHashMap();

        schedulerFactoryBeanCustomizers.stream()
                .sorted(AnnotationAwareOrderComparator.INSTANCE)
                .peek(customizer -> mergedProperties.putAll(customizer.quartzProperties()))
                .forEach(customizer -> customizer.customize(schedulerFactoryBean));

        Properties properties = new Properties();
        properties.putAll(mergedProperties);
        schedulerFactoryBean.setQuartzProperties(properties);
        return schedulerFactoryBean;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Configuration
    static class DefaultFactoryBeanConfiguration {

        @Bean
        SchedulerFactoryBeanCustomizer defaultFactoryBeanCustomizer(QuartzProperties quartzProperties,
                                                                    ObjectProvider<JobDetail[]> jobDetailsProvider,
                                                                    ObjectProvider<Trigger[]> triggersProvider,
                                                                    ObjectProvider<Map<String, Calendar>> calendarsProvider) {
            return new DefaultQuartzCustomizer(quartzProperties,
                    jobDetailsProvider.getIfAvailable(),
                    triggersProvider.getIfAvailable(),
                    calendarsProvider.getIfAvailable());
        }


        @Slf4j
        @AllArgsConstructor
        @Order(0)
        private static final class DefaultQuartzCustomizer implements SchedulerFactoryBeanCustomizer {

            private final QuartzProperties quartzProperties;
            private final JobDetail[] jobDetails;
            private final Trigger[] triggers;
            private final Map<String, Calendar> calendars;

            @Override
            public void customize(SchedulerFactoryBean bean) {
                LOG.info("{}Registering Quartz defaults", LOG_PREFIX);

                if (jobDetails != null) {
                    bean.setJobDetails(jobDetails);
                }
                if (triggers != null) {
                    bean.setTriggers(triggers);
                }
                if (calendars != null) {
                    bean.setCalendars(calendars);
                }
            }

            @Override
            public Map<String, String> quartzProperties() {
                return quartzProperties.getProperties();
            }
        }
    }

    @Configuration
    @ConditionalOnProperty(prefix = "neostarter.quartz", name = "forceRamJobStore", havingValue = "true")
    protected static class QuartzRamJobConfiguration {

        @Bean
        public SchedulerFactoryBeanCustomizer dataSourceCustomizer() {
            return new QuartzDatasourceCustomizer();
        }

        @Slf4j
        @Order(LOWEST_PRECEDENCE)
        private static final class QuartzDatasourceCustomizer implements SchedulerFactoryBeanCustomizer {

            @Override
            public void customize(SchedulerFactoryBean bean) {
                LOG.info("{}Forcing RAM Job", LOG_PREFIX);
            }

            @Override
            public Map<String, String> quartzProperties() {
                return ImmutableMap.of("org.quartz.jobStore.class", "org.quartz.simpl.RAMJobStore");
            }
        }
    }

    @Configuration
    @ConditionalOnBean(DataSource.class)
    @ConditionalOnMissingClass("com.novemberain.quartz.mongodb.MongoDBJobStore")
    @ConditionalOnProperty(prefix = "neostarter.quartz", name = "forceRamJobStore", havingValue = "false")
    static class QuartzDataSourceConfiguration {

        @Bean
        public SchedulerFactoryBeanCustomizer dataSourceCustomizer(DataSource dataSource) {
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

    @Configuration
    @ConditionalOnClass(MongoDBJobStore.class)
    @EnableConfigurationProperties(MongoProperties.class)
    @ConditionalOnProperty(prefix = "neostarter.quartz", name = "forceRamJobStore", havingValue = "false")
    static class QuartzMongoDbConfiguration {

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
}