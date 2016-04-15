package com.neoteric.starter.swagger;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("neostarter.swagger")
@Getter
@Setter
public class SwaggerProperties {

    private final Contact contact = new Contact();

    /**
     * Enable com.neoteric.starter.swagger switch
     */
    private boolean enabled = true;

    /**
     * Should generated com.neoteric.starter.swagger.json be formatted
     */
    private boolean prettyPrint = true;

    /**
     * Provides the version of the application API
     */
    private String version = "1";

    /**
     * The title of the application.
     */
    private String title;

    /**
     * A URL to the terms of service used for the API.
     */
    private String termsOfServiceUrl;
    /**
     * A short description of the application.
     */
    private String description;

    /**
     * The license name used for the API. Has to come together with 'licenseUrl' to be applied.
     */
    private String license;

    /**
     * A URL to the license used for the API. MUST be in the format of a URL. Has to come together with 'license' to be applied.
     */
    private String licenseUrl;

    /**
     * Package to scan for Swagger resources
     */
    private String resourcePackage = "com.neoteric";


    @Getter
    @Setter
    public static class Contact {

        /**
         * The contact email for the exposed API.
         */
        private String email;

        /**
         * The contact name for the exposed API.
         */
        private String name;

        /**
         * The contact url for the exposed API.
         */
        private String url;

    }
}
