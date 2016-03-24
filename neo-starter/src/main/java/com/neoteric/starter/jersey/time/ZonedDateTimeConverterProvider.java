package com.neoteric.starter.jersey.time;

import org.glassfish.jersey.internal.inject.ExtractorException;
import org.glassfish.jersey.server.internal.LocalizationMessages;

import javax.inject.Singleton;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;

@Singleton
public class ZonedDateTimeConverterProvider implements ParamConverterProvider {

    @Override
    public <T> ParamConverter<T> getConverter(final Class<T> rawType,
                                              final Type genericType,
                                              final Annotation[] annotations) {
        if (rawType.equals(ZonedDateTime.class)) {
            return new ParamConverter<T>() {
                @Override
                public T fromString(String value) {
                    if (value == null || value.isEmpty()) {
                        return null;
                    }

                    try {
                        return rawType.cast(ZonedDateTime.parse(value));

                    } catch (DateTimeParseException ex) {
                        throw new ExtractorException("QueryParam value: '" + value + "' cannot be parsed to ZonedDateTime.");
                    }
                }

                @Override
                public String toString(T value) {
                    if (value == null) {
                        throw new IllegalArgumentException(LocalizationMessages.METHOD_PARAMETER_CANNOT_BE_NULL("value"));
                    }
                    return value.toString();
                }
            };
        }

        return null;
    }
}