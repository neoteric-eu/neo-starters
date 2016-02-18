package com.neoteric.starter.jersey;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("neostarter.jersey")
public class NeoStarterJerseyProperties {

    private String[] packagesToScan;

    public String[] getPackagesToScan() {
        return packagesToScan;
    }

    public void setPackagesToScan(String[] packagesToScan) {
        this.packagesToScan = packagesToScan;
    }
}
