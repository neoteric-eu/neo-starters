package com.neoteric.starter.mvc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.autoconfigure.web.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.web.OrderedHiddenHttpMethodFilter;
import org.springframework.boot.context.web.OrderedHttpPutFormContentFilter;
import org.springframework.boot.context.web.OrderedRequestContextFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.core.io.Resource;
import org.springframework.format.Formatter;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.validation.DefaultMessageCodesResolver;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.filter.HttpPutFormContentFilter;
import org.springframework.web.filter.RequestContextFilter;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.i18n.FixedLocaleResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.resource.*;
import org.springframework.web.servlet.view.BeanNameViewResolver;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.servlet.Servlet;
import java.util.*;

@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass({Servlet.class, DispatcherServlet.class,
        WebMvcConfigurerAdapter.class})
@ConditionalOnMissingBean(WebMvcConfigurationSupport.class)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 9)
@AutoConfigureAfter(DispatcherServletAutoConfiguration.class)
public class StarterMvcAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(HiddenHttpMethodFilter.class)
    public OrderedHiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new OrderedHiddenHttpMethodFilter();
    }

    @Bean
    @ConditionalOnMissingBean(HttpPutFormContentFilter.class)
    public OrderedHttpPutFormContentFilter httpPutFormContentFilter() {
        return new OrderedHttpPutFormContentFilter();
    }

    // Defined as a nested config to ensure WebMvcConfigurerAdapter is not read when not
    // on the classpath
    @Configuration
    @Import(EnableWebMvcConfiguration.class)
    @EnableConfigurationProperties({WebMvcProperties.class, ResourceProperties.class, StarterMvcProperties.class})
    public static class WebMvcAutoConfigurationAdapter extends WebMvcConfigurerAdapter {

        private static final Log logger = LogFactory
                .getLog(WebMvcConfigurerAdapter.class);

        @Autowired
        private ResourceProperties resourceProperties = new ResourceProperties();

        @Autowired
        private WebMvcProperties mvcProperties = new WebMvcProperties();

        @Autowired
        private ListableBeanFactory beanFactory;

        @Autowired
        private HttpMessageConverters messageConverters;

        @Autowired(required = false)
        ResourceHandlerRegistrationCustomizer resourceHandlerRegistrationCustomizer;

        @Override
        public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
            converters.addAll(this.messageConverters.getConverters());
        }

        @Override
        public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
            Long timeout = this.mvcProperties.getAsync().getRequestTimeout();
            if (timeout != null) {
                configurer.setDefaultTimeout(timeout);
            }
        }

        @Override
        public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
            Map<String, MediaType> mediaTypes = this.mvcProperties.getMediaTypes();
            for (Map.Entry<String, MediaType> mediaType : mediaTypes.entrySet()) {
                configurer.mediaType(mediaType.getKey(), mediaType.getValue());
            }
        }

        @Bean
        @ConditionalOnMissingBean
        public InternalResourceViewResolver defaultViewResolver() {
            InternalResourceViewResolver resolver = new InternalResourceViewResolver();
            resolver.setPrefix(this.mvcProperties.getView().getPrefix());
            resolver.setSuffix(this.mvcProperties.getView().getSuffix());
            return resolver;
        }

        @Bean
        @ConditionalOnMissingBean({RequestContextListener.class,
                RequestContextFilter.class})
        public RequestContextFilter requestContextFilter() {
            return new OrderedRequestContextFilter();
        }

        @Bean
        @ConditionalOnBean(View.class)
        public BeanNameViewResolver beanNameViewResolver() {
            BeanNameViewResolver resolver = new BeanNameViewResolver();
            resolver.setOrder(Ordered.LOWEST_PRECEDENCE - 10);
            return resolver;
        }

        @Bean
        @ConditionalOnBean(ViewResolver.class)
        @ConditionalOnMissingBean(name = "viewResolver", value = ContentNegotiatingViewResolver.class)
        public ContentNegotiatingViewResolver viewResolver(BeanFactory beanFactory) {
            ContentNegotiatingViewResolver resolver = new ContentNegotiatingViewResolver();
            resolver.setContentNegotiationManager(
                    beanFactory.getBean(ContentNegotiationManager.class));
            // ContentNegotiatingViewResolver uses all the other view resolvers to locate
            // a view so it should have a high precedence
            resolver.setOrder(Ordered.HIGHEST_PRECEDENCE);
            return resolver;
        }

        @Bean
        @ConditionalOnMissingBean
        @ConditionalOnProperty(prefix = "spring.mvc", name = "locale")
        public LocaleResolver localeResolver() {
            return new FixedLocaleResolver(this.mvcProperties.getLocale());
        }

        @Bean
        @ConditionalOnProperty(prefix = "spring.mvc", name = "date-format")
        public Formatter<Date> dateFormatter() {
            return new DateFormatter(this.mvcProperties.getDateFormat());
        }

        @Override
        public MessageCodesResolver getMessageCodesResolver() {
            if (this.mvcProperties.getMessageCodesResolverFormat() != null) {
                DefaultMessageCodesResolver resolver = new DefaultMessageCodesResolver();
                resolver.setMessageCodeFormatter(
                        this.mvcProperties.getMessageCodesResolverFormat());
                return resolver;
            }
            return null;
        }

        @Override
        public void addFormatters(FormatterRegistry registry) {
            for (Converter<?, ?> converter : getBeansOfType(Converter.class)) {
                registry.addConverter(converter);
            }
            for (GenericConverter converter : getBeansOfType(GenericConverter.class)) {
                registry.addConverter(converter);
            }
            for (Formatter<?> formatter : getBeansOfType(Formatter.class)) {
                registry.addFormatter(formatter);
            }
        }

        private <T> Collection<T> getBeansOfType(Class<T> type) {
            return this.beanFactory.getBeansOfType(type).values();
        }

        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            if (!this.resourceProperties.isAddMappings()) {
                logger.debug("Default resource handling disabled");
                return;
            }
            Integer cachePeriod = this.resourceProperties.getCachePeriod();
            if (!registry.hasMappingForPattern("/webjars/**")) {
                customizeResourceHandlerRegistration(
                        registry.addResourceHandler("/webjars/**")
                                .addResourceLocations(
                                        "classpath:/META-INF/resources/webjars/")
                                .setCachePeriod(cachePeriod));
            }
            String staticPathPattern = this.mvcProperties.getStaticPathPattern();
            if (!registry.hasMappingForPattern(staticPathPattern)) {
                customizeResourceHandlerRegistration(
                        registry.addResourceHandler(staticPathPattern)
                                .addResourceLocations(
                                        this.resourceProperties.getStaticLocations())
                                .setCachePeriod(cachePeriod));
            }
        }

        private void customizeResourceHandlerRegistration(
                ResourceHandlerRegistration registration) {
            if (this.resourceHandlerRegistrationCustomizer != null) {
                this.resourceHandlerRegistrationCustomizer.customize(registration);
            }
        }

        @Override
        public void addViewControllers(ViewControllerRegistry registry) {
            Resource page = this.resourceProperties.getWelcomePage();
            if (page != null) {
                logger.info("Adding welcome page: " + page);
                registry.addViewController("/").setViewName("forward:index.html");
            }
        }

    }

    /**
     * Configuration equivalent to {@code @EnableWebMvc}.
     */
    @Configuration
    public static class EnableWebMvcConfiguration extends DelegatingWebMvcConfiguration {

        @Autowired(required = false)
        private WebMvcProperties mvcProperties;

        @Autowired
        private StarterMvcProperties starterMvcProperties;

        @Autowired
        private ListableBeanFactory beanFactory;

        @Bean
        @Override
        public RequestMappingHandlerAdapter requestMappingHandlerAdapter() {
            RequestMappingHandlerAdapter adapter = super.requestMappingHandlerAdapter();
            adapter.setIgnoreDefaultModelOnRedirect(this.mvcProperties == null ? true
                    : this.mvcProperties.isIgnoreDefaultModelOnRedirect());
            return adapter;
        }

        @Bean
        @Primary
        @Override
        public RequestMappingHandlerMapping requestMappingHandlerMapping() {
            // Must be @Primary for MvcUriComponentsBuilder to work
            ClassNameAwareRequestMappingHandlerMapping handlerMapping = new ClassNameAwareRequestMappingHandlerMapping();
            handlerMapping.setClassSuffixToPrefix(starterMvcProperties.getClassSuffixToPrefix());
            handlerMapping.setCaseFormat(starterMvcProperties.getCaseFormat());
            handlerMapping.setOrder(0);
            handlerMapping.setInterceptors(getInterceptors());
            handlerMapping.setContentNegotiationManager(mvcContentNegotiationManager());
            handlerMapping.setCorsConfigurations(getCorsConfigurations());

            PathMatchConfigurer configurer = getPathMatchConfigurer();
            if (configurer.isUseSuffixPatternMatch() != null) {
                handlerMapping.setUseSuffixPatternMatch(configurer.isUseSuffixPatternMatch());
            }
            if (configurer.isUseRegisteredSuffixPatternMatch() != null) {
                handlerMapping.setUseRegisteredSuffixPatternMatch(configurer.isUseRegisteredSuffixPatternMatch());
            }
            if (configurer.isUseTrailingSlashMatch() != null) {
                handlerMapping.setUseTrailingSlashMatch(configurer.isUseTrailingSlashMatch());
            }
            if (configurer.getPathMatcher() != null) {
                handlerMapping.setPathMatcher(configurer.getPathMatcher());
            }
            if (configurer.getUrlPathHelper() != null) {
                handlerMapping.setUrlPathHelper(configurer.getUrlPathHelper());
            }
            return handlerMapping;
        }

        @Override
        protected ConfigurableWebBindingInitializer getConfigurableWebBindingInitializer() {
            try {
                return this.beanFactory.getBean(ConfigurableWebBindingInitializer.class);
            } catch (NoSuchBeanDefinitionException ex) {
                return super.getConfigurableWebBindingInitializer();
            }
        }
    }

    @Configuration
    @ConditionalOnEnabledResourceChain
    static class ResourceChainCustomizerConfiguration {

        @Bean
        public ResourceChainResourceHandlerRegistrationCustomizer resourceHandlerRegistrationCustomizer() {
            return new ResourceChainResourceHandlerRegistrationCustomizer();
        }

    }

    interface ResourceHandlerRegistrationCustomizer {

        void customize(ResourceHandlerRegistration registration);

    }

    private static class ResourceChainResourceHandlerRegistrationCustomizer
            implements ResourceHandlerRegistrationCustomizer {

        @Autowired
        private ResourceProperties resourceProperties = new ResourceProperties();

        @Override
        public void customize(ResourceHandlerRegistration registration) {
            ResourceProperties.Chain properties = this.resourceProperties.getChain();
            configureResourceChain(properties,
                    registration.resourceChain(properties.isCache()));
        }

        private void configureResourceChain(ResourceProperties.Chain properties,
                                            ResourceChainRegistration chain) {
            ResourceProperties.Strategy strategy = properties.getStrategy();
            if (strategy.getFixed().isEnabled() || strategy.getContent().isEnabled()) {
                chain.addResolver(getVersionResourceResolver(strategy));
            }
            if (properties.isGzipped()) {
                chain.addResolver(new GzipResourceResolver());
            }
            if (properties.isHtmlApplicationCache()) {
                chain.addTransformer(new AppCacheManifestTransformer());
            }
        }

        private ResourceResolver getVersionResourceResolver(
                ResourceProperties.Strategy properties) {
            VersionResourceResolver resolver = new VersionResourceResolver();
            if (properties.getFixed().isEnabled()) {
                String version = properties.getFixed().getVersion();
                String[] paths = properties.getFixed().getPaths();
                resolver.addFixedVersionStrategy(version, paths);
            }
            if (properties.getContent().isEnabled()) {
                String[] paths = properties.getContent().getPaths();
                resolver.addContentVersionStrategy(paths);
            }
            return resolver;
        }

    }

}