package com.neoteric.starter.exception.mapper;

import ch.qos.logback.classic.Level;
import com.neoteric.starter.jersey.validation.Violation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import javax.ws.rs.ext.Provider;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Provider
public class ConstraintViolationExceptionMapper extends AbstractExceptionMapper<ConstraintViolationException> {

    private static final Logger LOG = LoggerFactory.getLogger(ConstraintViolationExceptionMapper.class);

    @Override
    protected HttpStatus httpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    protected Logger logger() {
        return LOG;
    }

    @Override
    protected Level logLevel() {
        return Level.WARN;
    }

    @Override
    protected Object message(ConstraintViolationException violation) {
        Set<ConstraintViolation<?>> constraintViolations = violation.getConstraintViolations();
        return constraintViolations.stream().map(ViolationMapper.INSTANCE).collect(Collectors.toList());
    }

    private enum ViolationMapper implements Function<ConstraintViolation<?>, Violation> {
        INSTANCE {

            @Override
            public Violation apply(ConstraintViolation<?> cv) {
                Path propertyPath = cv.getPropertyPath();
                String pathWithoutMethodName = StreamSupport.stream(propertyPath.spliterator(), false)
                        .skip(1)
                        .map(Path.Node::toString)
                        .collect(Collectors.joining("."));
                return new Violation(pathWithoutMethodName,
                        cv.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName().toUpperCase(),
                        cv.getInvalidValue(),
                        cv.getMessage());
            }
        }
    }
}