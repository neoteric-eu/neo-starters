package com.neoteric.starter.saasmgr;

import com.neoteric.starter.saasmgr.auth.SaasMgrAuthenticationProvider;
import com.neoteric.starter.saasmgr.client.SaasMgrClient;
import com.neoteric.starter.saasmgr.filter.SaasMgrAuthenticationFilter;
import com.neoteric.starter.saasmgr.filter.SaasMgrAuthenticationMatcher;
import lombok.extern.slf4j.Slf4j;
import net.sf.ehcache.config.CacheConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import static com.neoteric.starter.saasmgr.SaasMgrStarterConstants.SAAS_MGR_AUTH_CACHE;
import static com.neoteric.starter.saasmgr.SaasMgrStarterConstants.SAAS_MGR_CACHE_MANAGER;

@Slf4j
@Configuration
@ConditionalOnWebApplication
@EnableConfigurationProperties(SaasMgrProperties.class)
@AutoConfigureAfter(CacheAutoConfiguration.class)
@EnableCaching
public class SaasMgrAutoConfiguration {

    @Autowired
    SaasMgrClient saasMgrClient;

    @Bean
    SaasMgrAuthenticationProvider saasMgrAuthenticationProvider() {
        return new SaasMgrAuthenticationProvider(saasMgrClient);
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(saasMgrAuthenticationProvider());
    }

    @Configuration
    @ConditionalOnProperty(prefix = "neostarter.saasmgr.cache", name = "enabled", havingValue = "false")
    static class SaasMgrNoCacheConfig extends CachingConfigurerSupport {

        @Bean(name = SAAS_MGR_CACHE_MANAGER)
        @Override
        public CacheManager cacheManager() {
            LOG.debug("{}Using NoOpCacheManager", SaasMgrStarterConstants.LOG_PREFIX);
            return new NoOpCacheManager();
        }
    }

    @Configuration
    @ConditionalOnProperty(prefix = "neostarter.saasmgr.cache", name = "enabled", matchIfMissing = true)
    static class SaasMgrCachingConfig extends CachingConfigurerSupport {

        @Autowired
        SaasMgrProperties saasMgrProperties;

        @Bean(destroyMethod = "shutdown")
        public net.sf.ehcache.CacheManager ehCacheManager() {
            CacheConfiguration cacheConfiguration = new CacheConfiguration(SAAS_MGR_AUTH_CACHE, 1000)
                    .timeToLiveSeconds(saasMgrProperties.getCache().getTimeToLiveSeconds());

            net.sf.ehcache.config.Configuration config = new net.sf.ehcache.config.Configuration();
            config.addCache(cacheConfiguration);

            return net.sf.ehcache.CacheManager.newInstance(config);
        }

        @Bean(name = SAAS_MGR_CACHE_MANAGER)
        @Override
        public CacheManager cacheManager() {
            LOG.debug("{}Using EhCacheCacheManager", SaasMgrStarterConstants.LOG_PREFIX);
            return new EhCacheCacheManager(ehCacheManager());
        }
    }

    @Configuration
    @EnableGlobalMethodSecurity(prePostEnabled = true)
    @Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
    static class SecurityConfig extends WebSecurityConfigurerAdapter {


        @Value("#{environment.getProperty('neostarter.mvc.api.path') ?: environment.getProperty('neostarter.saasmgr.api.path') ?: ''}")
        String apiPath;

        @Bean
        SaasMgrAuthenticationMatcher saasMgrAuthenticationMatcher() {
            return new SaasMgrAuthenticationMatcher(apiPath);
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            SaasMgrAuthenticationFilter filter = new SaasMgrAuthenticationFilter(saasMgrAuthenticationMatcher());
            http.addFilterBefore(filter, BasicAuthenticationFilter.class)
                    .csrf().disable();
        }
    }
}