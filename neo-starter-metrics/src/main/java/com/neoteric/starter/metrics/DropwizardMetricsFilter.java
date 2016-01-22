package com.neoteric.starter.metrics;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.util.StopWatch;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation based on {@link org.springframework.boot.actuate.autoconfigure.MetricsFilter}.
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
public final class DropwizardMetricsFilter extends OncePerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(DropwizardMetricsFilter.class);
    private static final String ATTRIBUTE_STOP_WATCH = DropwizardMetricsFilter.class.getName()
            + ".StopWatch";

    private static final int UNDEFINED_HTTP_STATUS = 999;

    private static final Set<PatternReplacer> KEY_REPLACERS;
    static {
        Set<PatternReplacer> replacements = new LinkedHashSet<>();
        replacements.add(new PatternReplacer("/", Pattern.LITERAL, "."));
        replacements.add(new PatternReplacer("..", Pattern.LITERAL, "."));
        KEY_REPLACERS = Collections.unmodifiableSet(replacements);
    }

    private final MetricRegistry metricRegistry;
    private String applicationPath;

    public DropwizardMetricsFilter(MetricRegistry metricRegistry, String applicationPath) {
        this.metricRegistry = metricRegistry;
        this.applicationPath = applicationPath;
    }

    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        return false;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = new UrlPathHelper().getPathWithinApplication(request);
        return !path.startsWith(applicationPath);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        StopWatch stopWatch = createStopWatchIfNecessary(request);
        String path = new UrlPathHelper().getPathWithinApplication(request);
        int status = HttpStatus.INTERNAL_SERVER_ERROR.value();
        try {
            chain.doFilter(request, response);
            status = getStatus(response);
        } finally {
            if (!request.isAsyncStarted()) {
                stopWatch.stop();
                request.removeAttribute(ATTRIBUTE_STOP_WATCH);
                recordTime(request, path, status, stopWatch.getTotalTimeMillis());
            }
        }
    }

    private StopWatch createStopWatchIfNecessary(HttpServletRequest request) {
        StopWatch stopWatch = (StopWatch) request.getAttribute(ATTRIBUTE_STOP_WATCH);
        if (stopWatch == null) {
            stopWatch = new StopWatch();
            stopWatch.start();
            request.setAttribute(ATTRIBUTE_STOP_WATCH, stopWatch);
        }
        return stopWatch;
    }

    private int getStatus(HttpServletResponse response) {
        try {
            return response.getStatus();
        } catch (Exception ex) {
            LOG.error("Unable to fetch Response status", ex);
            return UNDEFINED_HTTP_STATUS;
        }
    }

    private void recordTime(HttpServletRequest request, String path, int status, long time) {
        HttpStatus.Series series = getSeries(status);
        meter(series, getKey(path + "." + request.getMethod()));
        timer(getKey(path + "." + request.getMethod()), time);
    }

    private void meter(HttpStatus.Series series, String meterName) {
        if (HttpStatus.Series.SUCCESSFUL.equals(series)) {
            return;
        }
        try {
            Meter meter = this.metricRegistry.meter("meter" + meterName + "." + series.value() + "xx");
            meter.mark();
        } catch (Exception ex) {
            LOG.warn("Unable to mark {} meter {}}", series.name() + "xx", meterName, ex);
        }
    }

    private void timer(String timerName, long time) {
        try {
            Timer timer = this.metricRegistry.timer("timer" + timerName);
            timer.update(time, TimeUnit.MILLISECONDS);
        } catch (Exception ex) {
            LOG.warn("Unable to submit timer '" + timerName + "'", ex);
        }
    }

    private HttpStatus.Series getSeries(int status) {
        try {
            return HttpStatus.valueOf(status).series();
        } catch (Exception ex) {
            return null;
        }
    }

    private String getKey(String string) {
        // graphite compatible metric names
        String key = string;
        for (PatternReplacer replacer : KEY_REPLACERS) {
            key = replacer.apply(key);
        }
        if (key.endsWith(".")) {
            key = key + "root";
        }
        if (key.startsWith("_")) {
            key = key.substring(1);
        }
        return key;
    }

    private static class PatternReplacer {

        private final Pattern pattern;
        private final String replacement;

        PatternReplacer(String regex, int flags, String replacement) {
            this.pattern = Pattern.compile(regex, flags);
            this.replacement = replacement;
        }

        public String apply(String input) {
            return this.pattern.matcher(input)
                    .replaceAll(Matcher.quoteReplacement(this.replacement));
        }
    }
}