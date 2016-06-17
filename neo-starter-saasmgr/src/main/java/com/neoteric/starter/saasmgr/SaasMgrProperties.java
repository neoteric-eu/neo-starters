package com.neoteric.starter.saasmgr;

import com.neoteric.starter.utils.PrefixResolver;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
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

    @NestedConfigurationProperty
    private final ApiSaasProperties api = new ApiSaasProperties();

    @Setter
    public static class ApiSaasProperties {
        private String path;

        public String getPath() {
            return PrefixResolver.resolve(path);
        }
    }

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
        private String name;
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
