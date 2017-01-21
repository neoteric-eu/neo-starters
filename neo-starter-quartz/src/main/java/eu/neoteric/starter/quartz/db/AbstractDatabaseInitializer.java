package eu.neoteric.starter.quartz.db;

import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

public abstract class AbstractDatabaseInitializer {

    private static final String PLATFORM_PLACEHOLDER = "@@platform@@";

    private final DataSource dataSource;

    private final ResourceLoader resourceLoader;

    protected AbstractDatabaseInitializer(DataSource dataSource,
                                          ResourceLoader resourceLoader) {
        Assert.notNull(dataSource, "DataSource must not be null");
        Assert.notNull(resourceLoader, "ResourceLoader must not be null");
        this.dataSource = dataSource;
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    protected void initialize() {
        if (!isEnabled()) {
            return;
        }
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        String schemaLocation = getSchemaLocation();
        if (schemaLocation.contains(PLATFORM_PLACEHOLDER)) {
            String platform = getDatabaseName();
            schemaLocation = schemaLocation.replace(PLATFORM_PLACEHOLDER, platform);
        }
        populator.addScript(this.resourceLoader.getResource(schemaLocation));
        populator.setContinueOnError(true);
        DatabasePopulatorUtils.execute(populator, this.dataSource);
    }

    protected abstract boolean isEnabled();

    protected abstract String getSchemaLocation();

    protected String getDatabaseName() {
        try {
            String productName = JdbcUtils.commonDatabaseName(JdbcUtils
                    .extractDatabaseMetaData(this.dataSource, "getDatabaseProductName")
                    .toString());
            DatabaseDriver databaseDriver = DatabaseDriver.fromProductName(productName);
            if (databaseDriver == DatabaseDriver.UNKNOWN) {
                throw new IllegalStateException("Unable to detect database type");
            }
            return databaseDriver.getDriverClassName();
        } catch (MetaDataAccessException ex) {
            throw new IllegalStateException("Unable to detect database type", ex);
        }
    }

}