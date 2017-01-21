package eu.neoteric.starter.mvc.logging;

import eu.neoteric.starter.mvc.logging.ApiLoggingAspect;
import eu.neoteric.starter.mvc.logging.ApiLoggingAutoConfiguration;
import org.junit.After;
import org.junit.Test;
import org.springframework.boot.autoconfigure.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

public class ApiLoggingAutoConfigurationTest {

    private AnnotationConfigApplicationContext context;

    @After
    public void close() {
        if (this.context != null) {
            this.context.close();
        }
    }

    private void load(Class<?> config, String... environment) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        EnvironmentTestUtils.addEnvironment(context, environment);
        if (config != null) {
            context.register(config);
        }
        context.register(PropertyPlaceholderAutoConfiguration.class,
                ApiLoggingAutoConfiguration.class);
        context.refresh();
        this.context = context;
    }

    @Test
    public void loggingShouldBeEnabledByDefault() throws Exception {
        load(null);
        assertThat(this.context.getBeansOfType(ApiLoggingAspect.class)).hasSize(1);
    }

    @Test
    public void disableLogging() throws Exception {
        load(null, "neostarter.mvc.logging.enabled=false");
        assertThat(this.context.getBeansOfType(ApiLoggingAspect.class)).hasSize(0);
    }

}