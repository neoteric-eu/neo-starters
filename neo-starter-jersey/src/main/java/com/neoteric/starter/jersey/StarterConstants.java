package com.neoteric.starter.jersey;

public final class StarterConstants {

    private StarterConstants() {
        // Prevents instantiation of the class
    }

    public static final String REQUEST_ID = "REQUEST_ID";
    public static final String UTC = "UTC";
    public static final String SWAGGER_PACKAGE = "io.swagger.jaxrs.listing";
    public static final String LOG_PREFIX = "[NeoStarter] ";

    public static final class ConfigBeans {

        private ConfigBeans() {
            // Prevents instantiation of the class
        }

        public static final String JACKSON_JSR310_DATE_FORMAT = "jackson-jsr310-dateFormat";
    }
}