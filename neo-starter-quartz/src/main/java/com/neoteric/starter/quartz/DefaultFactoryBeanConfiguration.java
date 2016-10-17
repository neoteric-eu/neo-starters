package com.neoteric.starter.quartz;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Calendar;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.Arrays;
import java.util.Map;

import static com.neoteric.starter.quartz.StarterQuartzConstants.LOG_PREFIX;

@Configuration
class DefaultFactoryBeanConfiguration {

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
                LOG.info("{}Registering Job Details: {}", LOG_PREFIX, Arrays.toString(jobDetails));
                bean.setJobDetails(jobDetails);
            }
            if (triggers != null) {
                LOG.info("{}Registering Triggers: {}", LOG_PREFIX, Arrays.toString(triggers));
                bean.setTriggers(triggers);
            }
            if (calendars != null) {
                LOG.info("{}Registering Calendars: {}", LOG_PREFIX, calendars);
                bean.setCalendars(calendars);
            }
        }

        @Override
        public Map<String, String> quartzProperties() {
            return quartzProperties.getProperties();
        }
    }
}
