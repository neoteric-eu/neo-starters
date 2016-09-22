package com.neoteric.starter.quartz;

import com.novemberain.quartz.mongodb.MongoDBJobStore;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.listeners.TriggerListenerSupport;
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
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static com.neoteric.starter.quartz.StarterQuartzConstants.LOG_PREFIX;
import static org.springframework.boot.Banner.Mode.LOG;

@Slf4j
@Configuration
@ConditionalOnClass({Scheduler.class, SchedulerFactoryBean.class})
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
@AllArgsConstructor
@EnableConfigurationProperties(QuartzProperties.class)
public class QuartzAutoConfiguration implements ApplicationContextAware {

    private final List<SchedulerFactoryBeanCustomizer> schedulerFactoryBeanCustomizers;
    private ApplicationContext applicationContext;

    @Bean
    @ConditionalOnProperty(prefix = "neostarter.quartz", name = "enabled", havingValue = "false")
    public SchedulerFactoryBean noOpschedulerFactoryBean() {
        LOG.info("{}Quartz disabled", LOG_PREFIX);
        SchedulerFactoryBean bean = new SchedulerFactoryBean();
        bean.setGlobalTriggerListeners(new VetoAllListener());
        return bean;
    }

    private static class VetoAllListener extends TriggerListenerSupport {

        @Override
        public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
            return true;
        }

        @Override
        public String getName() {
            return "vetoAll";
        }
    }

    @Bean
    @ConditionalOnProperty(prefix = "neostarter.quartz", name = "enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean
    public SchedulerFactoryBean schedulerFactoryBean() {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setJobFactory(
                new AutowiringSpringBeanJobFactory(this.applicationContext.getAutowireCapableBeanFactory()));
        schedulerFactoryBeanCustomizers.stream()
                .sorted(AnnotationAwareOrderComparator.INSTANCE)
                .forEach(customizer -> customizer.customize(schedulerFactoryBean));
        return schedulerFactoryBean;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Slf4j
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


        @AllArgsConstructor
        private static final class DefaultQuartzCustomizer implements SchedulerFactoryBeanCustomizer, Ordered {

            private final QuartzProperties quartzProperties;
            private final JobDetail[] jobDetails;
            private final Trigger[] triggers;
            private final Map<String, Calendar> calendars;

            @Override
            public void customize(SchedulerFactoryBean bean) {
                DefaultFactoryBeanConfiguration.LOG.info("{}Registering Quartz defaults", LOG_PREFIX);

                if (jobDetails != null) {
                    bean.setJobDetails(jobDetails);
                }
                if (triggers != null) {
                    bean.setTriggers(triggers);
                }
                if (calendars != null) {
                    bean.setCalendars(calendars);
                }
                if (quartzProperties.getProperties() != null) {
                    Properties properties = new Properties();
                    properties.putAll(quartzProperties.getProperties());
                    bean.setQuartzProperties(properties);
                }
            }

            @Override
            public int getOrder() {
                return 0;
            }
        }
    }

    @Slf4j
    @Configuration
    @ConditionalOnProperty(prefix = "neostarter.quartz", name = "forceRamJobStore", havingValue = "true")
    protected static class QuartzRamJobConfiguration {

        @Bean
        public SchedulerFactoryBeanCustomizer dataSourceCustomizer() {
            return new QuartzDatasourceCustomizer();
        }

        private static final class QuartzDatasourceCustomizer implements SchedulerFactoryBeanCustomizer, Ordered {

            @Override
            public void customize(SchedulerFactoryBean bean) {
                QuartzDataSourceConfiguration.LOG.info("{}Forcing RAM Job", LOG_PREFIX);
                Properties props = new Properties();
                props.put("org.quartz.jobStore.class", "org.quartz.simpl.RAMJobStore");
                bean.setQuartzProperties(props);
            }

            @Override
            public int getOrder() {
                return LOWEST_PRECEDENCE;
            }
        }
    }

    @Slf4j
    @Configuration
    @ConditionalOnBean(DataSource.class)
    @ConditionalOnMissingClass("com.novemberain.quartz.mongodb.MongoDBJobStore")
    @ConditionalOnProperty(prefix = "neostarter.quartz", name = "forceRamJobStore", havingValue = "false")
    protected static class QuartzDataSourceConfiguration {

        @Bean
        public SchedulerFactoryBeanCustomizer dataSourceCustomizer(DataSource dataSource) {
            return new QuartzDatasourceCustomizer(dataSource);
        }

        @AllArgsConstructor
        private static final class QuartzDatasourceCustomizer implements SchedulerFactoryBeanCustomizer, Ordered {

            private final DataSource dataSource;

            @Override
            public void customize(SchedulerFactoryBean bean) {
                QuartzDataSourceConfiguration.LOG.info("{}Registering Quartz datasource", LOG_PREFIX);
                bean.setDataSource(dataSource);
            }

            @Override
            public int getOrder() {
                return 1;
            }
        }
    }

    @Slf4j
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

        @AllArgsConstructor
        private static final class QuartzMongoDbCustomizer implements SchedulerFactoryBeanCustomizer, Ordered {

            private final QuartzProperties quartzProperties;
            private final MongoProperties mongoProperties;

            @Override
            public void customize(SchedulerFactoryBean bean) {
                QuartzMongoDbConfiguration.LOG.info("{}Registering Quartz MongoDB defaults", LOG_PREFIX);
                Properties props = quartzProperties.getMongo().buildFromMongoProperties(mongoProperties);
                bean.setQuartzProperties(props);
            }

            @Override
            public int getOrder() {
                return 1;
            }
        }
    }
}