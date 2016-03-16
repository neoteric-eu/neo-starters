package com.neoteric.starter.mongo;

import com.mongodb.Mongo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.CustomConversions;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Configuration
@AutoConfigureBefore({MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
@ConditionalOnClass({Mongo.class, MongoTemplate.class, ZonedDateTime.class})
public class MongoConvertersAutoConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(MongoConvertersAutoConfiguration.class);

    @Bean
    public CustomConversions customConversions() {
        List<Converter<?, ?>> converters = new ArrayList<>();
        converters.add(DateToZonedDateTimeConverter.INSTANCE);
        converters.add(ZonedDateTimeToDateConverter.INSTANCE);
        converters.add(StringToZoneIdConverter.INSTANCE);
        converters.add(ZoneIdToStringConverter.INSTANCE);
        LOG.debug("{}Registering ZonedDateTime converters.", StarterMongoConstants.LOG_PREFIX);
        return new CustomConversions(converters);
    }

    public enum DateToZonedDateTimeConverter implements Converter<Date, ZonedDateTime> {
        INSTANCE;

        @Override
        public ZonedDateTime convert(Date source) {
            return source == null ? null : source.toInstant().atZone(ZoneId.systemDefault());
        }
    }

    public enum ZonedDateTimeToDateConverter implements Converter<ZonedDateTime, Date> {
        INSTANCE;

        @Override
        public Date convert(ZonedDateTime source) {
            return source == null ? null : Date.from(source.toInstant());
        }
    }

    public enum StringToZoneIdConverter implements Converter<String, ZoneId> {
        INSTANCE;

        @Override
        public ZoneId convert(String source) {
            return source == null ? null : ZoneId.of(source);
        }
    }

    public enum ZoneIdToStringConverter implements Converter<ZoneId, String> {
        INSTANCE;

        @Override
        public String convert(ZoneId source) {
            return source == null ? null : source.getId();
        }
    }
}
