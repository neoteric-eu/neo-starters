package com.neoteric.starter.auth.saasmgr.test;

import com.neoteric.starter.auth.SaasMgrSecurityAutoConfiguration;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

public class InvalidateSaasMgrCacheListener extends AbstractTestExecutionListener {

    @Override
    public void afterTestClass(TestContext testContext) throws Exception {
        CacheManager cacheManager = testContext.getApplicationContext().getBean(SaasMgrSecurityAutoConfiguration.SAAS_MGR_CACHE_MANAGER, CacheManager.class);
        Cache cache = cacheManager.getCache(SaasMgrSecurityAutoConfiguration.SAAS_MGR_AUTH_CACHE);
        cache.clear();
    }
}
