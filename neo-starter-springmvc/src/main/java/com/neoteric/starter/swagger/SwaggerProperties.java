package com.neoteric.starter.swagger;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("neostarter.swagger")
public class SwaggerProperties {

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
     * The contact information for the exposed API.
     */
    private String contact = "backend@neoteric.eu";

    /**
     * The transfer protocol for the operation. Values MUST be from the list: "http", "https", "ws", "wss".
     */
    private String[] schemes = {"http"};

    /**
     * The title of the application.
     */
    private String title;

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

    public String[] getSchemes() {
        return schemes;
    }

    public void setSchemes(String[] schemes) {
        this.schemes = schemes;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getLicenseUrl() {
        return licenseUrl;
    }

    public void setLicenseUrl(String licenseUrl) {
        this.licenseUrl = licenseUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getResourcePackage() {
        return resourcePackage;
    }

    public void setResourcePackage(String resourcePackage) {
        this.resourcePackage = resourcePackage;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public boolean isPrettyPrint() {
        return prettyPrint;
    }

    public void setPrettyPrint(boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

}
