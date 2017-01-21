package eu.neoteric.starter.metrics;


import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.TimeUnit;

@ConfigurationProperties("neostarter.metrics.report")
public class MetricsReportProperties {

    /**
     * Main flag to enable metrics reporting
     */
    private boolean enabled = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    ElasticSearch elastic = new ElasticSearch();

    public ElasticSearch getElastic() {
        return elastic;
    }

    public static class ElasticSearch {

        /**
         * Flag to enable reporting to Elasticsearch
         */
        private boolean enabled = true;

        /**
         * Array of Elasticsearch hosts to report Metrics to
         */
        private String[] hosts = {"localhost:9200"};

        /**
         * The timeout to wait for until a connection attempt is and the next host is tried (in ms). Default is 1000.
         */
        private int timeout = 1000;

        /**
         * Convert all the rates to a certain timeunit, defaults to seconds
         */
        private TimeUnit ratesTimeUnit = TimeUnit.SECONDS;

        /**
         * Convert all the durations to a certain timeunit, defaults to milliseconds
         */
        private TimeUnit duriationsTimeUnit = TimeUnit.MILLISECONDS;

        /**
         * Configure a prefix for each metric name. Optional, but useful to identify single hosts
         */
        private String prefix;

        /**
         * Amount of time units between reports. Default is 60 (seconds)
         */
        private long timePeriod = 60;

        /**
         * Time unit to use along with timePeriod between reports. Default: SECONDS.
         * Options are: DAYS, HOURS, MICROSECONDS, MILLISECONDS, NANOSECONDS, MINUTES, SECONDS.
         */

        private TimeUnit timeUnit = TimeUnit.SECONDS;

        public TimeUnit getRatesTimeUnit() {
            return ratesTimeUnit;
        }

        public void setRatesTimeUnit(TimeUnit ratesTimeUnit) {
            this.ratesTimeUnit = ratesTimeUnit;
        }

        public TimeUnit getDuriationsTimeUnit() {
            return duriationsTimeUnit;
        }

        public void setDuriationsTimeUnit(TimeUnit duriationsTimeUnit) {
            this.duriationsTimeUnit = duriationsTimeUnit;
        }

        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        public int getTimeout() {
            return timeout;
        }

        public void setTimeout(int timeout) {
            this.timeout = timeout;
        }

        public long getTimePeriod() {
            return timePeriod;
        }

        public void setTimePeriod(long timePeriod) {
            this.timePeriod = timePeriod;
        }

        public TimeUnit getTimeUnit() {
            return timeUnit;
        }

        public void setTimeUnit(TimeUnit timeUnit) {
            this.timeUnit = timeUnit;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String[] getHosts() {
            return hosts;
        }

        public void setHosts(String[] hosts) {
            this.hosts = hosts;
        }
    }
}
