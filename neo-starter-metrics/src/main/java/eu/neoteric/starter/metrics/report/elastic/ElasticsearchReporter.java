/*
 * Licensed to Elasticsearch under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. ElasticSearch licenses this
 * file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package eu.neoteric.starter.metrics.report.elastic;

import com.codahale.metrics.*;
import com.codahale.metrics.Timer;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import eu.neoteric.starter.metrics.report.elastic.percolation.Notifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.codahale.metrics.MetricRegistry.name;
import static eu.neoteric.starter.metrics.report.elastic.JsonMetrics.*;

@SuppressWarnings({"squid:S00103", // Lines should not be too long
                    "squid:S1943", // Classes and methods that rely on the default system encoding should not be used
                    "squid:MethodCyclomaticComplexity",
                    "squid:S134", // Control flow statements "if", "for", "while", "switch" and "try" should not be nested too deeply
                    "squid:S00107",
                    "squid:S1141", // Try-catch blocks should not be nested
                    "squid:S1226", // Method parameters, caught exceptions and foreach variables should not be reassigned
                    "squid:S00112",
                    "squid:S1155", // Collection.isEmpty() should be used to test for emptiness
                    "squid:S1067", // Expressions should not be too complex
                    "squid:S1200", // Classes should not be coupled to too many other classes (Single Responsibility Principle)
                    "squid:S1943"
})
public class ElasticsearchReporter extends ScheduledReporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchReporter.class);
    private static final int DEFAULT_BULK_SIZE = 2500;
    private static final int DEFAULT_TIMEOUT = 1000;

    private final String[] hosts;
    private final Clock clock;
    private final String prefix;
    private final String index;
    private final int bulkSize;
    private final int timeout;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ObjectWriter writer;
    private MetricFilter percolationFilter;
    private Notifier notifier;
    private String currentIndexName;
    private SimpleDateFormat indexDateFormat;
    private boolean checkedForIndexTemplate;

    public ElasticsearchReporter(MetricRegistry registry, String[] hosts, int timeout,
                                 String index, String indexDateFormat, int bulkSize, Clock clock, String prefix, TimeUnit rateUnit, TimeUnit durationUnit,
                                 MetricFilter filter, MetricFilter percolationFilter, Notifier percolationNotifier, String timestampFieldname, Map<String, Object> additionalFields) {
        super(registry, "elasticsearch-reporter", filter, rateUnit, durationUnit);
        this.hosts = hosts;
        this.index = index;
        this.bulkSize = bulkSize;
        this.clock = clock;
        this.prefix = prefix;
        this.timeout = timeout;
        if (indexDateFormat != null && indexDateFormat.length() > 0) {
            this.indexDateFormat = new SimpleDateFormat(indexDateFormat);
        }
        if (percolationNotifier != null && percolationFilter != null) {
            this.percolationFilter = percolationFilter;
            this.notifier = percolationNotifier;
        }
        if (timestampFieldname == null || timestampFieldname.trim().length() == 0) {
            LOGGER.error("Timestampfieldname {} is not valid, using default @timestamp", timestampFieldname);
            timestampFieldname = "@timestamp";
        }

        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(SerializationFeature.CLOSE_CLOSEABLE, false);
        // auto closing means, that the objectmapper is closing after the first write call, which does not work for bulk requests
        objectMapper.configure(JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT, false);
        objectMapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
        objectMapper.registerModule(new AfterburnerModule());
        objectMapper.registerModule(new MetricsElasticsearchModule(rateUnit, durationUnit, timestampFieldname, additionalFields));
        writer = objectMapper.writer();
        checkForIndexTemplate();
    }

    public static Builder forRegistry(MetricRegistry registry) {
        return new Builder(registry);
    }

    @Override
    public void report(SortedMap<String, Gauge> gauges,
                       SortedMap<String, Counter> counters,
                       SortedMap<String, Histogram> histograms,
                       SortedMap<String, Meter> meters,
                       SortedMap<String, Timer> timers) {

        // nothing to do if we dont have any metrics to report
        if (gauges.isEmpty() && counters.isEmpty() && histograms.isEmpty() && meters.isEmpty() && timers.isEmpty()) {
            LOGGER.info("All metrics empty, nothing to report");
            return;
        }

        if (!checkedForIndexTemplate) {
            checkForIndexTemplate();
        }
        final long timestamp = clock.getTime() / 1000;

        currentIndexName = index;
        if (indexDateFormat != null) {
            currentIndexName += "-" + indexDateFormat.format(new Date(timestamp * 1000));
        }

        try {
            HttpURLConnection connection;
            try {
                connection = openConnection("/_bulk", "POST");
            } catch (ElasticsearchConnectionException e) {
                LOGGER.error("Could not connect to any configured elasticsearch instances: {}", Arrays.asList(hosts), e);
                return;
            }

            List<JsonMetrics.JsonMetric> percolationMetrics = new ArrayList<>();
            AtomicInteger entriesWritten = new AtomicInteger(0);

            for (Map.Entry<String, Gauge> entry : gauges.entrySet()) {
                if (entry.getValue().getValue() != null) {
                    JsonMetrics.JsonMetric jsonMetric = new JsonMetrics.JsonGauge(name(prefix, entry.getKey()), timestamp, entry.getValue());
                    connection = writeJsonMetricAndRecreateConnectionIfNeeded(jsonMetric, connection, entriesWritten);
                    addJsonMetricToPercolationIfMatching(jsonMetric, percolationMetrics);
                }
            }

            for (Map.Entry<String, Counter> entry : counters.entrySet()) {
                JsonCounter jsonMetric = new JsonCounter(name(prefix, entry.getKey()), timestamp, entry.getValue());
                connection = writeJsonMetricAndRecreateConnectionIfNeeded(jsonMetric, connection, entriesWritten);
                addJsonMetricToPercolationIfMatching(jsonMetric, percolationMetrics);
            }

            for (Map.Entry<String, Histogram> entry : histograms.entrySet()) {
                JsonHistogram jsonMetric = new JsonHistogram(name(prefix, entry.getKey()), timestamp, entry.getValue());
                connection = writeJsonMetricAndRecreateConnectionIfNeeded(jsonMetric, connection, entriesWritten);
                addJsonMetricToPercolationIfMatching(jsonMetric, percolationMetrics);
            }

            for (Map.Entry<String, Meter> entry : meters.entrySet()) {
                JsonMeter jsonMetric = new JsonMeter(name(prefix, entry.getKey()), timestamp, entry.getValue());
                connection = writeJsonMetricAndRecreateConnectionIfNeeded(jsonMetric, connection, entriesWritten);
                addJsonMetricToPercolationIfMatching(jsonMetric, percolationMetrics);
            }

            for (Map.Entry<String, Timer> entry : timers.entrySet()) {
                JsonTimer jsonMetric = new JsonTimer(name(prefix, entry.getKey()), timestamp, entry.getValue());
                connection = writeJsonMetricAndRecreateConnectionIfNeeded(jsonMetric, connection, entriesWritten);
                addJsonMetricToPercolationIfMatching(jsonMetric, percolationMetrics);
            }

            closeConnection(connection);

            // execute the notifier impl, in case percolation found matches
            if (percolationMetrics.size() > 0 && notifier != null) {
                for (JsonMetric jsonMetric : percolationMetrics) {
                    List<String> matches = getPercolationMatches(jsonMetric);
                    for (String match : matches) {
                        notifier.notify(jsonMetric, match);
                    }
                }
            }
            // catch the exception to make sure we do not interrupt the live application
        } catch (ElasticsearchConnectionException e) {
            LOGGER.error("Couldnt report to elasticsearch server", e);
        } catch (IOException e) {
            LOGGER.error("Couldnt report to elasticsearch server", e);
        }
    }

    private List<String> getPercolationMatches(JsonMetric jsonMetric) throws IOException {
        HttpURLConnection connection;
        try {
            connection = openConnection("/" + currentIndexName + "/" + jsonMetric.type() + "/_percolate", "POST");
        } catch (ElasticsearchConnectionException e) {
            LOGGER.error("Could not connect to any configured elasticsearch instances for percolation: {}", Arrays.asList(hosts), e);
            return Collections.emptyList();
        }

        Map<String, Object> data = new HashMap<>(1);
        data.put("doc", jsonMetric);
        objectMapper.writeValue(connection.getOutputStream(), data);
        closeConnection(connection);

        if (connection.getResponseCode() != HttpStatus.OK.value()) {
            throw new RuntimeException("Error percolating " + jsonMetric);
        }

        Map<String, Object> input = objectMapper.readValue(connection.getInputStream(), new TypeReference<Map<String, Object>>() {
        });
        List<String> matches = new ArrayList<>();
        if (input.containsKey("matches") && input.get("matches") instanceof List) {
            List<Map<String, String>> foundMatches = (List<Map<String, String>>) input.get("matches");
            for (Map<String, String> entry : foundMatches) {
                if (entry.containsKey("_id")) {
                    matches.add(entry.get("_id"));
                }
            }
        }

        return matches;
    }

    private void addJsonMetricToPercolationIfMatching(JsonMetric<? extends Metric> jsonMetric, List<JsonMetric> percolationMetrics) {
        if (percolationFilter != null && percolationFilter.matches(jsonMetric.name(), jsonMetric.value())) {
            percolationMetrics.add(jsonMetric);
        }
    }

    private HttpURLConnection writeJsonMetricAndRecreateConnectionIfNeeded(JsonMetric jsonMetric, HttpURLConnection connection,
                                                                           AtomicInteger entriesWritten) throws IOException {
        writeJsonMetric(jsonMetric, writer, connection.getOutputStream());
        return createNewConnectionIfBulkSizeReached(connection, entriesWritten.incrementAndGet());
    }

    private void closeConnection(HttpURLConnection connection) throws IOException {
        connection.getOutputStream().close();
        connection.disconnect();

        // we have to call this, otherwise out HTTP data does not get send, even though close()/disconnect was called
        // Ceterum censeo HttpUrlConnection esse delendam
        if (connection.getResponseCode() != HttpStatus.OK.value()) {
            LOGGER.error("Reporting returned code {} {}: {}", connection.getResponseCode(), connection.getResponseMessage());
        }
    }

    private HttpURLConnection createNewConnectionIfBulkSizeReached(HttpURLConnection connection, int entriesWritten) throws IOException {
        if (entriesWritten % bulkSize == 0) {
            closeConnection(connection);
            return openConnection("/_bulk", "POST");
        }

        return connection;
    }

    private void writeJsonMetric(JsonMetric jsonMetric, ObjectWriter writer, OutputStream out) throws IOException {
        writer.writeValue(out, new MetricsElasticsearchModule.BulkIndexOperationHeader(currentIndexName, jsonMetric.type()));
        out.write("\n".getBytes());
        writer.writeValue(out, jsonMetric);
        out.write("\n".getBytes());

        out.flush();
    }

    private HttpURLConnection openConnection(String uri, String method) {
        for (String host : hosts) {
            try {
                URL templateUrl = new URL("http://" + host + uri);
                HttpURLConnection connection = (HttpURLConnection) templateUrl.openConnection();
                connection.setRequestMethod(method);
                connection.setConnectTimeout(timeout);
                connection.setUseCaches(false);
                if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method)) {
                    connection.setDoOutput(true);
                }
                connection.connect();

                return connection;
            } catch (IOException e) {
                LOGGER.error("Error connecting to {}: {}", host, e);
            }
        }
        throw new ElasticsearchConnectionException("Error connecting to elasticsearch host(s): " + Arrays.toString(hosts));
    }

    private void checkForIndexTemplate() {
        try {
            HttpURLConnection connection = openConnection("/_template/metrics_template", "HEAD");
            connection.disconnect();
            boolean isTemplateMissing = connection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND;

            // nothing there, lets create it
            if (isTemplateMissing) {
                LOGGER.debug("No metrics template found in elasticsearch. Adding...");
                HttpURLConnection putTemplateConnection = openConnection("/_template/metrics_template", "PUT");
                JsonGenerator json = new JsonFactory().createGenerator(putTemplateConnection.getOutputStream());
                json.writeStartObject();
                json.writeStringField("template", index + "*");
                json.writeObjectFieldStart("mappings");

                json.writeObjectFieldStart("_default_");
                json.writeObjectFieldStart("_all");
                json.writeBooleanField("enabled", false);
                json.writeEndObject();
                json.writeObjectFieldStart("properties");
                json.writeObjectFieldStart("name");
                json.writeObjectField("type", "string");
                json.writeObjectField("index", "not_analyzed");
                json.writeEndObject();
                json.writeEndObject();
                json.writeEndObject();

                json.writeEndObject();
                json.writeEndObject();
                json.flush();

                putTemplateConnection.disconnect();
                if (putTemplateConnection.getResponseCode() != HttpStatus.OK.value()) {
                    LOGGER.error("Error adding metrics template to elasticsearch: {}/{}" + putTemplateConnection.getResponseCode(), putTemplateConnection.getResponseMessage());
                }
            }
            checkedForIndexTemplate = true;
        } catch (IOException e) {
            LOGGER.error("Error when checking/adding metrics template to elasticsearch", e);
        }
    }

    public static final class Builder {
        private final MetricRegistry registry;
        private Clock clock;
        private String prefix;
        private TimeUnit rateUnit;
        private TimeUnit durationUnit;
        private MetricFilter filter;
        private String[] hosts = new String[]{"localhost:9200"};
        private String index = "metrics";
        private String indexDateFormat = "yyyy-MM";
        private int bulkSize = DEFAULT_BULK_SIZE;
        private Notifier percolationNotifier;
        private MetricFilter percolationFilter;
        private int timeout = DEFAULT_TIMEOUT;
        private String timestampFieldname = "@timestamp";
        private Map<String, Object> additionalFields;

        private Builder(MetricRegistry registry) {
            this.registry = registry;
            this.clock = Clock.defaultClock();
            this.prefix = null;
            this.rateUnit = TimeUnit.SECONDS;
            this.durationUnit = TimeUnit.MILLISECONDS;
            this.filter = MetricFilter.ALL;
        }

        public Builder withClock(Clock clock) {
            this.clock = clock;
            return this;
        }

        public Builder prefixedWith(String prefix) {
            this.prefix = prefix;
            return this;
        }

        public Builder convertRatesTo(TimeUnit rateUnit) {
            this.rateUnit = rateUnit;
            return this;
        }

        public Builder convertDurationsTo(TimeUnit durationUnit) {
            this.durationUnit = durationUnit;
            return this;
        }

        public Builder filter(MetricFilter filter) {
            this.filter = filter;
            return this;
        }

        public Builder hosts(String... hosts) {
            this.hosts = hosts;
            return this;
        }

        public Builder timeout(int timeout) {
            this.timeout = timeout;
            return this;
        }

        public Builder index(String index) {
            this.index = index;
            return this;
        }

        public Builder indexDateFormat(String indexDateFormat) {
            this.indexDateFormat = indexDateFormat;
            return this;
        }

        public Builder bulkSize(int bulkSize) {
            this.bulkSize = bulkSize;
            return this;
        }

        public Builder percolationFilter(MetricFilter percolationFilter) {
            this.percolationFilter = percolationFilter;
            return this;
        }

        public Builder percolationNotifier(Notifier notifier) {
            this.percolationNotifier = notifier;
            return this;
        }

        public Builder timestampFieldname(String fieldName) {
            this.timestampFieldname = fieldName;
            return this;
        }

        public Builder additionalFields(Map<String, Object> additionalFields) {
            this.additionalFields = additionalFields;
            return this;
        }

        public ElasticsearchReporter build() {
            return new ElasticsearchReporter(registry,
                    hosts,
                    timeout,
                    index,
                    indexDateFormat,
                    bulkSize,
                    clock,
                    prefix,
                    rateUnit,
                    durationUnit,
                    filter,
                    percolationFilter,
                    percolationNotifier,
                    timestampFieldname,
                    additionalFields);
        }
    }
}
