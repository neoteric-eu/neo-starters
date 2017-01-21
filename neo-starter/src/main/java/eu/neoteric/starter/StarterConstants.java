package eu.neoteric.starter;

public final class StarterConstants {

    public static final String REQUEST_ID_HEADER = "REQUEST_ID";
    public static final String UTC = "UTC";
    public static final String LOG_PREFIX = "[NeoStarterMVC] ";

    private StarterConstants() {
        // Prevents instantiation of the class
    }

    public static final class ConfigBeans {

        public static final String JACKSON_JSR310_DATE_FORMAT = "eu.neoteric.starter.jackson-jsr310-dateFormat";

        private ConfigBeans() {
            // Prevents instantiation of the class
        }

    }
}