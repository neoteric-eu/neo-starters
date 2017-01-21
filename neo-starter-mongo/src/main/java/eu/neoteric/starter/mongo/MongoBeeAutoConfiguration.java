package eu.neoteric.starter.mongo;

import com.github.mongobee.Mongobee;
import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Optional;

@Configuration
@AutoConfigureAfter(MongoAutoConfiguration.class)
@EnableConfigurationProperties(MongoBeeProperties.class)
@RequiredArgsConstructor
public class MongoBeeAutoConfiguration {

    private final MongoBeeProperties mongoBeeProperties;
    private final BeanFactory beanFactory;

    @Bean
    @ConditionalOnProperty(prefix = "neostarter.mongobee", name = "enabled", matchIfMissing = true, havingValue = "true")
    public Mongobee mongobee(Environment environment, MongoProperties mongoProperties) {
        String customUri = mongoBeeProperties.getUri();
        String uri = Strings.isNullOrEmpty(customUri) ? mongoProperties.getUri() : customUri;
        Mongobee runner = new Mongobee(uri);
        runner.setSpringEnvironment(environment);

        String scanPackage = Optional.ofNullable(mongoBeeProperties.getPackageToScan())
                .orElseGet(() -> autoConfigurationPackage(beanFactory)
                .orElseThrow(() -> new IllegalStateException("Need to provide package to scan")));
        runner.setChangeLogsScanPackage(scanPackage);
        runner.setEnabled(true);
        return runner;
    }

    private Optional<String> autoConfigurationPackage(BeanFactory beanFactory) {
        return AutoConfigurationPackages.has(this.beanFactory) ?
                Optional.of(AutoConfigurationPackages.get(beanFactory).get(0)) : Optional.empty();
    }
}
