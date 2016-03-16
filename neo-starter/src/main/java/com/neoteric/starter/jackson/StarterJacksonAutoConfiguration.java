package com.neoteric.starter.jackson;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.joda.cfg.JacksonJodaDateFormat;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import com.neoteric.starter.StarterConstants;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
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

import static com.neoteric.starter.StarterConstants.ConfigBeans.JACKSON_JSR310_DATE_FORMAT;

@Configuration
@Slf4j
public class StarterJacksonAutoConfiguration {

    @Configuration
    @ConditionalOnClass({Jackson2ObjectMapperBuilder.class, DateTime.class,
            ZonedDateTimeSerializer.class, JacksonJodaDateFormat.class})
    @AutoConfigureBefore(JacksonAutoConfiguration.class)
    @PropertySource("classpath:jackson-defaults.properties")
    static class ZonedDateTimeJacksonConfiguration {

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
            LOG.debug("{}ZonedDateTime Jackson format: {}", StarterConstants.LOG_PREFIX, dateTimeFormatter);
            module.addSerializer(ZonedDateTime.class, new ZonedDateTimeSerializer(dateTimeFormatter));
            return module;
        }
    }

    @Configuration
    @AutoConfigureAfter(JacksonAutoConfiguration.class)
    static class JsonHandlerAutoConfiguration {

        @Bean
        JsonParser jsonHandler(ObjectMapper objectMapper) {
            return new JsonParser(objectMapper);
        }
    }
}