package eu.neoteric.starter.request.tracing;

import com.google.common.collect.Lists;
import eu.neoteric.starter.mvc.StarterMvcProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.support.InterceptingHttpAccessor;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableConfigurationProperties(StarterMvcProperties.class)
public class RequestIdTracingAutoConfiguration {

    @Autowired
    StarterMvcProperties starterMvcProperties;

    @Autowired(required = false)
    List<RequestIdListener> requestIdListeners = Lists.newArrayList();

    @Autowired(required = false)
    private List<InterceptingHttpAccessor> clients = Lists.newArrayList();

    @Bean
    MDCHystrixConcurrencyStrategy requestIdHystrixConcurrencyStrategy() {
        return new MDCHystrixConcurrencyStrategy();
    }

    @Bean
    @ConditionalOnMissingBean(RequestIdGenerator.class)
    RequestIdGenerator requestIdGenerator() {
        return new UuidRequestIdGenerator();
    }

    @Bean
    FilterRegistrationBean registerRequestIdFilter(RequestIdGenerator generator) {
        RequestIdFilter filter = new RequestIdFilter(generator, requestIdListeners, starterMvcProperties.getApi().getPath());
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(filter);
        filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return filterRegistrationBean;
    }

    @Bean
    public InitializingBean restTemplateRequestIdInitializer() {
        return () -> {
            for (InterceptingHttpAccessor client : clients) {
                final List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>(client.getInterceptors());
                interceptors.add(new RestTemplateRequestIdInterceptor());
                client.setInterceptors(interceptors);
            }
        };
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}