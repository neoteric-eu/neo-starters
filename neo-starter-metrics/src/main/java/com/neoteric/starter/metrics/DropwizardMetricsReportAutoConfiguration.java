package com.neoteric.starter.metrics;

import com.codahale.metrics.MetricRegistry;
import com.neoteric.starter.metrics.StarterMetricsConstants;
import com.neoteric.starter.metrics.report.elastic.ElasticsearchConnectionException;
import com.neoteric.starter.metrics.report.elastic.ElasticsearchReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;


@Configuration
@EnableConfigurationProperties(MetricsReportProperties.class)
@ConditionalOnProperty(value = "neostarter.metrics.report.enabled", matchIfMissing = true)
@AutoConfigureAfter(DropwizardMetricsAutoConfiguration.class)
public class DropwizardMetricsReportAutoConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(DropwizardMetricsReportAutoConfiguration.class);

    @Configuration
    @ConditionalOnProperty(value = "neoteric.metrics.report.elastic.enabled", matchIfMissing = true)
    @EnableConfigurationProperties(MetricsReportProperties.class)
    @AutoConfigureAfter(DropwizardMetricsReportAutoConfiguration.class)
    static class ElasticSearchReporterAutoConfiguration {

        @Autowired
        MetricsReportProperties metricsReportProperties;

        @Autowired(required = false)
        ElasticsearchReporter elasticReporter;

        @Bean
        ElasticsearchReporter registerElasticSearchReporter(MetricRegistry metricRegistry) {
            MetricsReportProperties.ElasticSearch elasticProperties = metricsReportProperties.getElastic();
            try {
                ElasticsearchReporter reporter = ElasticsearchReporter.forRegistry(metricRegistry)
                        .hosts(elasticProperties.getHosts())
                        .timeout(elasticProperties.getTimeout())
                        .prefixedWith(elasticProperties.getPrefix())
                        .convertRatesTo(elasticProperties.getRatesTimeUnit())
                        .convertDurationsTo(elasticProperties.getDuriationsTimeUnit())
                        .build();
                LOG.debug("{}Reporter: {}", reporter);
                return reporter;
            } catch (ElasticsearchConnectionException e) {
                LOG.error("Unable to create ElasticsearchReporter", e);
                return null;
            }
        }

        @PostConstruct
        public void runElasticReporter() throws Exception {
            if (elasticReporter == null) {
                LOG.warn("{}Could not start Elasticsearch Metrics reporter - not found.", StarterMetricsConstants.LOG_PREFIX);
                return;
            }

            MetricsReportProperties.ElasticSearch elasticProperties = metricsReportProperties.getElastic();
            long period = elasticProperties.getTimePeriod();
            TimeUnit timeUnit = elasticProperties.getTimeUnit();
            LOG.debug("{}ElasticSearch Metrics reporter started. Reporting every {} {}", StarterMetricsConstants.LOG_PREFIX, period, timeUnit);
            elasticReporter.start(period, timeUnit);
        }
    }


}