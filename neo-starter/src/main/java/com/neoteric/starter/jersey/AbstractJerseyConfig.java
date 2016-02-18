package com.neoteric.starter.jersey;

import com.neoteric.starter.Constants;
import com.neoteric.starter.swagger.SwaggerProperties;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Arrays;

public abstract class AbstractJerseyConfig extends ResourceConfig {

    private static final String STARTER_EXCEPTION_MAPPERS_PACKAGE = "com.neoteric.starter.exception.mapper";
    private static final Logger LOG = LoggerFactory.getLogger(AbstractJerseyConfig.class);

    @Autowired
    protected SwaggerProperties swaggerProperties;

    @Autowired
    protected NeoStarterJerseyProperties starterJerseyProperties;

    /**
     * Register application specific Jersey resources.
     */
    protected abstract void configure();

    @PostConstruct // In constructor we can't inject properties
    public void register() {
        logRegister(MultiPartFeature.class);
        logRegister(ObjectMapperProvider.class);
        logPackages(STARTER_EXCEPTION_MAPPERS_PACKAGE);
        String[] packagesToScan = starterJerseyProperties.getPackagesToScan();
        if (packagesToScan != null && packagesToScan.length > 0) {
            logPackages(starterJerseyProperties.getPackagesToScan());
        }

        if (swaggerProperties.isEnabled()) {
            this.packages(Constants.SWAGGER_PACKAGE);
        }
        configure();
    }

    private void logRegister(final Class<?> componentClass) {
        LOG.debug("{} Jersey registers {}", Constants.LOG_PREFIX, componentClass.getName());
        register(componentClass);
    }
    private void logPackages(String... packages) {
        LOG.debug("{} Jersey registers packages {}", Constants.LOG_PREFIX, Arrays.toString(packages));
        packages(packages);
    }
}