package com.neoteric.starter.saasmgr;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties("neostarter.saasmgr")
@Getter
@Setter
public class SaasMgrProperties {

    /**
     * Saas Manager address, when RestTemplate is used (Feign not available or disabled)
     */
    private String address;

    @NestedConfigurationProperty
    private final FeignProperties feign = new FeignProperties();

    @Getter
    @Setter
    public static class FeignProperties {

        /**
         * Enable Feign client for Saas Manager connectivity
         */
        private boolean enabled = true;

        /**
         * Name of Feign client
         */
        private String name = "saasManager";
    }

    @NestedConfigurationProperty
    private final CacheProperties cache = new CacheProperties();

    @Getter
    @Setter
    public static class CacheProperties {

        /**
         * Enable SaaS Manager authentication caching
         */
        private boolean enabled = true;

        /**
         * CacheProperties timeout in seconds
         */
        private int timeToLiveSeconds = 5;
    }
}
