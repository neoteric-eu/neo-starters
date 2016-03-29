package com.neoteric.starter.jersey.time;

import org.glassfish.jersey.internal.inject.ExtractorException;
import org.springframework.util.StringUtils;

import javax.inject.Singleton;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Singleton
public class ZonedDateTimeConverterProvider implements ParamConverterProvider {

    @Override
    public <T> ParamConverter<T> getConverter(final Class<T> rawType,
                                              final Type genericType,
                                              final Annotation[] annotations) {
        if (rawType.equals(ZonedDateTime.class)) {
            return (ParamConverter<T>) new ZonedDateTimeParamConverter();
        }

        return null;
    }

    private static class ZonedDateTimeParamConverter implements ParamConverter<ZonedDateTime> {

        @Override
        public ZonedDateTime fromString(String value) {
            if (StringUtils.isEmpty(value)) {
                return null;
            }
            try {
                return ZonedDateTime.parse(value);
            } catch (DateTimeParseException ex) {
                throw new ExtractorException("Parameter '" + value + "' cannot be parsed to ZonedDateTime");
            }
        }

        @Override
        public String toString(ZonedDateTime value) {
            if (value == null) {
                return null;
            }
            //TODO: Use injected formatter
            return value.format(DateTimeFormatter.ISO_INSTANT);
        }
    }
}