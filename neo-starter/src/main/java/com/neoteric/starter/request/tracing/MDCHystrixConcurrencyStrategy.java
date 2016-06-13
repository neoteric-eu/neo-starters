package com.neoteric.starter.request.tracing;

import com.neoteric.starter.StarterConstants;
import com.netflix.hystrix.strategy.HystrixPlugins;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import com.netflix.hystrix.strategy.eventnotifier.HystrixEventNotifier;
import com.netflix.hystrix.strategy.executionhook.HystrixCommandExecutionHook;
import com.netflix.hystrix.strategy.metrics.HystrixMetricsPublisher;
import com.netflix.hystrix.strategy.properties.HystrixPropertiesStrategy;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.util.Map;
import java.util.concurrent.Callable;

@Slf4j
public class MDCHystrixConcurrencyStrategy extends HystrixConcurrencyStrategy {

	public MDCHystrixConcurrencyStrategy() {
		try {
			HystrixPlugins plugins = HystrixPlugins.getInstance();
			HystrixConcurrencyStrategy concurrencyStrategy = plugins
					.getConcurrencyStrategy();
			if (concurrencyStrategy instanceof MDCHystrixConcurrencyStrategy) {
				return;
			}

			HystrixCommandExecutionHook commandExecutionHook = plugins
					.getCommandExecutionHook();
			HystrixEventNotifier eventNotifier = plugins.getEventNotifier();
			HystrixMetricsPublisher metricsPublisher = plugins.getMetricsPublisher();
			HystrixPropertiesStrategy propertiesStrategy = plugins
					.getPropertiesStrategy();
			logCurrentStateOfHystrixPlugins(eventNotifier, metricsPublisher,
					propertiesStrategy);
			HystrixPlugins.reset();
			plugins.registerConcurrencyStrategy(this);
			plugins.registerCommandExecutionHook(commandExecutionHook);
			plugins.registerEventNotifier(eventNotifier);
			plugins.registerMetricsPublisher(metricsPublisher);
			plugins.registerPropertiesStrategy(propertiesStrategy);
		}
		catch (IllegalStateException e) {
			LOG.error("Failed to register MDC Hystrix Concurrency Strategy", e);
		}
	}

	private void logCurrentStateOfHystrixPlugins(HystrixEventNotifier eventNotifier,
			HystrixMetricsPublisher metricsPublisher,
			HystrixPropertiesStrategy propertiesStrategy) {
		LOG.debug(
				"{}Current Hystrix plugins configuration [concurrencyStrategy [{}], eventNotifier [{}], metricPublisher [{}], "
						+ "propertiesStrategy [{}]]", StarterConstants.LOG_PREFIX, this,
				eventNotifier, metricsPublisher, propertiesStrategy);
		LOG.debug("{}Registering MDC Hystrix Concurrency Strategy.",
				StarterConstants.LOG_PREFIX);
	}

	@Override
	public <T> Callable<T> wrapCallable(Callable<T> callable) {
		return new MDCCallable<T>(callable);
	}

	private static class MDCCallable<T> implements Callable<T> {

		private final Callable<T> actual;
		private final Map<String, String> parentMdcContextMap;

		public MDCCallable(Callable<T> callable) {
			this.actual = callable;
			parentMdcContextMap = MDC.getCopyOfContextMap();
		}

		@Override
		public T call() throws Exception {
			Map<String, String> existingContextMap = MDC.getCopyOfContextMap();
			try {
				if (parentMdcContextMap != null) {
					MDC.setContextMap(parentMdcContextMap);
				}
				return actual.call();
			}
			finally {
				if (existingContextMap != null) {
					MDC.setContextMap(existingContextMap);
				}
			}
		}
	}
}
