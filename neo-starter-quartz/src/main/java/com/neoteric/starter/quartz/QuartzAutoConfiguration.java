package com.neoteric.starter.quartz;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import static com.neoteric.starter.quartz.StarterQuartzConstants.LOG_PREFIX;

@Slf4j
@Configuration
@ConditionalOnClass({Scheduler.class, SchedulerFactoryBean.class})
@AutoConfigureAfter({DataSourceAutoConfiguration.class,
        DefaultFactoryBeanConfiguration.class,
        QuartzDataSourceConfiguration.class,
        QuartzRamJobConfiguration.class,
        QuartzMongoDbConfiguration.class})
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

    @Bean
    @ConditionalOnProperty(prefix = "neostarter.quartz", name = "enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean
    public SchedulerFactoryBean schedulerFactoryBean() {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setJobFactory(
                new AutowiringSpringBeanJobFactory(this.applicationContext.getAutowireCapableBeanFactory()));

        Map<String, String> mergedProperties = Maps.newHashMap();

        schedulerFactoryBeanCustomizers.stream()
                .sorted(AnnotationAwareOrderComparator.INSTANCE)
                .peek(customizer -> {
                    Map<String, String> quartzProperties = customizer.quartzProperties();
                    if (!quartzProperties.isEmpty()) {
                        LOG.info("{}Adding {} properties: {}", LOG_PREFIX, customizer.getClass().getName(),
                                quartzProperties);
                        mergedProperties.putAll(quartzProperties);
                    }
                })
                .forEach(customizer -> customizer.customize(schedulerFactoryBean));

        Properties properties = new Properties();
        properties.putAll(mergedProperties);
        LOG.info("{}Merged properties sent to Quartz [{}]", LOG_PREFIX, mergedProperties);
        schedulerFactoryBean.setQuartzProperties(properties);
        return schedulerFactoryBean;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}