package com.neoteric.starter.jackson;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.joda.cfg.JacksonJodaDateFormat;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import com.neoteric.starter.Constants;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static com.neoteric.starter.Constants.ConfigBeans.JACKSON_JSR310_DATE_FORMAT;

@Configuration
@PropertySource("classpath:jackson-defaults.properties")
@AutoConfigureBefore(JacksonAutoConfiguration.class)
public class JacksonDefaultsAutoConfiguration {

    @Configuration
    @ConditionalOnClass({Jackson2ObjectMapperBuilder.class, DateTime.class,
            ZonedDateTimeSerializer.class, JacksonJodaDateFormat.class})
    @AutoConfigureBefore(JacksonAutoConfiguration.class)
    static class ZonedDateTimeJacksonConfiguration {

        private static final Logger LOG = LoggerFactory.getLogger(ZonedDateTimeJacksonConfiguration.class);

        @Autowired
        private JacksonProperties jacksonProperties;

        @Bean(name = JACKSON_JSR310_DATE_FORMAT)
        @ConditionalOnMissingBean(name = JACKSON_JSR310_DATE_FORMAT)
        public DateTimeFormatter dateTimeFormatter() {
            return DateTimeFormatter.ISO_INSTANT;
        }

        @Bean
        public Module zonedDateTimeSerializationModule(
                @Qualifier(JACKSON_JSR310_DATE_FORMAT) DateTimeFormatter dateTimeFormatter) {
            SimpleModule module = new SimpleModule();
            LOG.debug("{}ZonedDateTime Jackson format: {}", Constants.LOG_PREFIX, dateTimeFormatter);
            module.addSerializer(ZonedDateTime.class, new ZonedDateTimeSerializer(dateTimeFormatter));
            return module;
        }
    }
}