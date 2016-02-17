package com.neoteric.starter.auth;

import com.neoteric.starter.auth.saasmgr.SaasMgrAuthenticationProvider;
import com.neoteric.starter.auth.saasmgr.SaasMgrConnector;
import com.neoteric.starter.auth.saasmgr.filter.SaasMgrAuthenticationFilter;
import net.sf.ehcache.config.CacheConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.jersey.JerseyProperties;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@ConditionalOnWebApplication
@EnableConfigurationProperties(SaasMgrCacheProperties.class)
@EnableCaching
public class SaasMgrSecurityAutoConfiguration {

    public static final String SAAS_MGR_AUTH_CACHE = "saasMgrAuthCache";
    public static final String SAAS_MGR_CACHE_MANAGER = "saasMgrCacheManager";

    @Configuration
    @ConditionalOnProperty(prefix = "neostarter.saasmgr.cache", name = "enabled", matchIfMissing = true)
    public static class SaasMgrCachingConfig extends CachingConfigurerSupport {

        @Autowired
        SaasMgrCacheProperties saasMgrCacheProperties;

        @Bean(destroyMethod = "shutdown")
        public net.sf.ehcache.CacheManager ehCacheManager() {
            CacheConfiguration cacheConfiguration = new CacheConfiguration(SAAS_MGR_AUTH_CACHE, 1000)
                    .timeToLiveSeconds(saasMgrCacheProperties.getTimeToLiveSeconds());

            net.sf.ehcache.config.Configuration config = new net.sf.ehcache.config.Configuration();
            config.addCache(cacheConfiguration);

            return net.sf.ehcache.CacheManager.newInstance(config);
        }

        @Bean(name = SAAS_MGR_CACHE_MANAGER)
        @Override
        public CacheManager cacheManager() {
            return new EhCacheCacheManager(ehCacheManager());
        }
    }

    @Configuration
    @ConditionalOnProperty(prefix = "neostarter.saasmgr.cache", name = "enabled", havingValue = "false")
    public static class SaasMgrNoCacheConfig extends CachingConfigurerSupport {

        @Bean(name = SAAS_MGR_CACHE_MANAGER)
        @Override
        public CacheManager cacheManager() {
            return new NoOpCacheManager();
        }
    }

    @Bean
    SaasMgrConnector saasMgrConnector() {
        return new SaasMgrConnector();
    }

    @Bean
    SaasMgrAuthenticationProvider saasMgrAuthenticationProvider() {
        return new SaasMgrAuthenticationProvider(saasMgrConnector());
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(saasMgrAuthenticationProvider());
    }

    @Configuration
    @EnableGlobalMethodSecurity(prePostEnabled = true)
    @Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
    @EnableFeignClients
    public static class SecurityConfig extends WebSecurityConfigurerAdapter {

        @Autowired
        JerseyProperties jerseyProperties;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            SaasMgrAuthenticationFilter filter = new SaasMgrAuthenticationFilter(jerseyProperties.getApplicationPath());
            http.addFilterBefore(filter, BasicAuthenticationFilter.class);
        }
    }
}
