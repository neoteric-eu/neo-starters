package eu.neoteric.starter.quartz;

import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.Map;

import static eu.neoteric.starter.quartz.StarterQuartzConstants.LOG_PREFIX;
import static org.springframework.core.Ordered.LOWEST_PRECEDENCE;

@Configuration
@ConditionalOnProperty(prefix = "neostarter.quartz", name = "forceRamJobStore", havingValue = "true")
class QuartzRamJobConfiguration {

    @Bean
    public SchedulerFactoryBeanCustomizer forcedRamJobCustomizer() {
        return new QuartzRamJobCustomizer();
    }

    @Slf4j
    @Order(LOWEST_PRECEDENCE)
    private static final class QuartzRamJobCustomizer implements SchedulerFactoryBeanCustomizer {

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
