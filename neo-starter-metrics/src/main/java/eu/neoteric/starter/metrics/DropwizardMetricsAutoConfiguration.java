package eu.neoteric.starter.metrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jvm.ClassLoadingGaugeSet;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jersey.JerseyProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@ConditionalOnClass(MetricRegistry.class)
//TODO: Add check if using Jersey?
@EnableConfigurationProperties(JerseyProperties.class)
public class DropwizardMetricsAutoConfiguration {

    @Autowired
    JerseyProperties jerseyProperties;

    @Autowired
    MetricRegistry metricRegistry;

    @Bean
    // Try to disable here execution of MetricFilterAutoConfiguration // Issue to SpringBoot
    public DropwizardMetricsFilter metricFilter() {
        return new DropwizardMetricsFilter(metricRegistry, jerseyProperties.getApplicationPath());
    }

    @PostConstruct
    public void addJvmMetrics() {
        metricRegistry.register("jvm.gc", new GarbageCollectorMetricSet());
        metricRegistry.register("jvm.mem", new MemoryUsageGaugeSet());
        metricRegistry.register("jvm.classloader", new ClassLoadingGaugeSet());
        metricRegistry.register("jvm.thread-states", new ThreadStatesGaugeSet());
    }
}
