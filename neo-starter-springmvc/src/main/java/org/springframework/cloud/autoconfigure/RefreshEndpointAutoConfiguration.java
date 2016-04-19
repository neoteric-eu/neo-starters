package org.springframework.cloud.autoconfigure;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.actuate.autoconfigure.ConditionalOnEnabledHealthIndicator;
import org.springframework.boot.actuate.autoconfigure.EndpointAutoConfiguration;
import org.springframework.boot.actuate.endpoint.Endpoint;
import org.springframework.boot.actuate.endpoint.InfoEndpoint;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.bootstrap.config.PropertySourceBootstrapConfiguration;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.cloud.context.properties.ConfigurationPropertiesRebinder;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.cloud.context.restart.RestartEndpoint;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.cloud.endpoint.RefreshEndpoint;
import org.springframework.cloud.endpoint.event.RefreshEventListener;
import org.springframework.cloud.health.RefreshScopeHealthIndicator;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Changed due to incompatibilty with Spring Boot 1.4.0.M2:
 * https://github.com/spring-cloud/spring-cloud-commons/issues/108
 */
@Configuration
@ConditionalOnClass(Endpoint.class)
@AutoConfigureAfter(EndpointAutoConfiguration.class)
public class RefreshEndpointAutoConfiguration {

	@ConditionalOnBean(EndpointAutoConfiguration.class)
	@Bean
	InfoEndpointRebinderConfiguration infoEndpointRebinderConfiguration() {
		return new InfoEndpointRebinderConfiguration();
	}

	@ConditionalOnMissingBean
	@ConditionalOnEnabledHealthIndicator("refresh")
	@Bean
	RefreshScopeHealthIndicator refreshScopeHealthIndicator(RefreshScope scope,
			ConfigurationPropertiesRebinder rebinder) {
		return new RefreshScopeHealthIndicator(scope, rebinder);
	}

	protected static class RestartEndpointWithoutIntegration {

		@Bean
		@ConditionalOnMissingBean
		public RestartEndpoint restartEndpoint() {
			return new RestartEndpoint();
		}
	}

	@Bean
	@ConfigurationProperties("endpoints.pause")
	public RestartEndpoint.PauseEndpoint pauseEndpoint(RestartEndpoint restartEndpoint) {
		return restartEndpoint.getPauseEndpoint();
	}

	@Bean
	@ConfigurationProperties("endpoints.resume")
	public RestartEndpoint.ResumeEndpoint resumeEndpoint(
			RestartEndpoint restartEndpoint) {
		return restartEndpoint.getResumeEndpoint();
	}

	@Configuration
	@ConditionalOnProperty(value = "endpoints.refresh.enabled", matchIfMissing = true)
	@ConditionalOnBean(PropertySourceBootstrapConfiguration.class)
	protected static class RefreshEndpointConfiguration {

		@Bean
		@ConditionalOnMissingBean
		public RefreshEndpoint refreshEndpoint(ContextRefresher contextRefresher) {
			return new RefreshEndpoint(contextRefresher);
		}

		@Bean
		public RefreshEventListener refreshEventListener(
				RefreshEndpoint refreshEndpoint) {
			return new RefreshEventListener(refreshEndpoint);
		}

	}

	private static class InfoEndpointRebinderConfiguration
			implements ApplicationListener<EnvironmentChangeEvent>, BeanPostProcessor {

		@Autowired
		private ConfigurableEnvironment environment;

		private Map<String, Object> map = new LinkedHashMap<String, Object>();

		@Override
		public void onApplicationEvent(EnvironmentChangeEvent event) {
			for (String key : event.getKeys()) {
				if (key.startsWith("info.")) {
					this.map.put(key.substring("info.".length()),
							this.environment.getProperty(key));
				}
			}
		}

		@Override
		public Object postProcessAfterInitialization(Object bean, String beanName) {
			if (bean instanceof InfoEndpoint) {
				return infoEndpoint((InfoEndpoint) bean);
			}
			return bean;
		}

		@Override
		public Object postProcessBeforeInitialization(Object bean, String beanName) {
			return bean;
		}

		private InfoEndpoint infoEndpoint(InfoEndpoint endpoint) {
			return new InfoEndpoint(Lists.newArrayList()) {
				@Override
				public Info invoke() {
					return new Info.Builder()
							.withDetails(endpoint.invoke().getDetails())
							.withDetails(InfoEndpointRebinderConfiguration.this.map)
							.build();
				}
			};
		}

	}

}