package com.neoteric.starter.saasmgr;

import lombok.extern.slf4j.Slf4j;
import net.sf.ehcache.config.CacheConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.neoteric.starter.saasmgr.SaasMgrStarterConstants.SAAS_MGR_AUTH_CACHE;
import static com.neoteric.starter.saasmgr.SaasMgrStarterConstants.SAAS_MGR_CACHE_MANAGER;

@Slf4j
@Configuration
@ConditionalOnWebApplication
@EnableConfigurationProperties(SaasMgrProperties.class)
@AutoConfigureAfter(CacheAutoConfiguration.class)
@EnableCaching
public class SaasMgrCacheAutoConfiguration {

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
			CacheConfiguration cacheConfiguration = new CacheConfiguration(
					SAAS_MGR_AUTH_CACHE, 1000).timeToLiveSeconds(saasMgrProperties
					.getCache().getTimeToLiveSeconds());

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
}