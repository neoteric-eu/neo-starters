package eu.neoteric.starter.quartz.db;

import eu.neoteric.starter.quartz.QuartzProperties;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;

import javax.sql.DataSource;

public class QuartzDatabaseInitializer extends AbstractDatabaseInitializer {

    private final QuartzProperties quartzProperties;

    public QuartzDatabaseInitializer(DataSource dataSource,
                                     ResourceLoader resourceLoader,
                                     QuartzProperties properties) {
        super(dataSource, resourceLoader);
        Assert.notNull(properties, "QuartzProperties must not be null.");
        this.quartzProperties = properties;
    }

    @Override
    protected boolean isEnabled() {
        return this.quartzProperties.getJdbc().isInitialize();
    }

    @Override
    protected String getSchemaLocation() {
        return this.quartzProperties.getJdbc().getSchema();
    }
}