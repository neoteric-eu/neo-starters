package com.neoteric.starter.mvc;

import com.google.common.base.CaseFormat;
import com.neoteric.starter.utils.PrefixResolver;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.Map;

@ConfigurationProperties(prefix = "neostarter.mvc")
@Getter
@Setter
public class StarterMvcProperties {

    @NestedConfigurationProperty
    private final RestErrorHandling errorHandling = new RestErrorHandling();
    @NestedConfigurationProperty
    private final ApiProperties api = new ApiProperties();

    @Setter
    public static class ApiProperties {
        private String path;

        @NestedConfigurationProperty
        private final ResourceProperties resources = new ResourceProperties();

        public String getPath() {
            return PrefixResolver.resolve(path);
        }

        public ResourceProperties getResources() {
            return resources;
        }
    }


    @Getter
    @Setter
    public static class ResourceProperties {
        private String defaultPrefix;
        private CaseFormat caseFormat = CaseFormat.LOWER_HYPHEN;
        private String classNamePattern;
    }

    @Getter
    @Setter
    public static class RestErrorHandling {
        private boolean enabled = true;
        private boolean defaultHandlersEnabled = true;
        private Map<String, String> causeMapping;
    }
}
