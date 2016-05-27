package com.neoteric.starter.mvc.errorhandling.handlers.common;

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableMap;
import com.neoteric.starter.mvc.errorhandling.handler.RestExceptionHandler;
import com.neoteric.starter.mvc.errorhandling.handler.RestExceptionHandlerProvider;
import com.neoteric.starter.mvc.validation.Violation;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RestExceptionHandlerProvider(httpStatus = HttpStatus.BAD_REQUEST, logLevel = Level.WARN)
@SuppressWarnings("squid:S2095")
public class MethodArgumentNotValidExceptionHandler implements RestExceptionHandler<MethodArgumentNotValidException> {

    private static final String VIOLATIONS = "violations";

    @Override
    public Object errorMessage(MethodArgumentNotValidException exception, HttpServletRequest request) {
        BindingResult bindingResult = exception.getBindingResult();
        int errorCount = bindingResult.getErrorCount();

        return String.join("", bindingResult.getObjectName(), " has ", String.valueOf(errorCount), " validation error",
                errorCount > 1 ? "s" : "");
    }

    @Override
    public Map<String, Object> additionalInfo(MethodArgumentNotValidException exception, HttpServletRequest request) {
        BindingResult bindingResult = exception.getBindingResult();
        List<Violation> validationErrors = Stream.concat(bindingResult.getGlobalErrors().stream()
                        .map(ObjectErrorMapper.INSTANCE),
                bindingResult.getFieldErrors().stream()
                        .map(FieldErrorMapper.INSTANCE))
                .collect(Collectors.toList());
        return ImmutableMap.of(VIOLATIONS, validationErrors);
    }

    private enum ObjectErrorMapper implements Function<ObjectError, Violation> {
        INSTANCE {
            @Override
            public Violation apply(ObjectError objectError) {
                return Violation.builder()
                        .property(objectError.getObjectName())
                        .message(objectError.getDefaultMessage())
                        .type(getType(objectError))
                        .build();
            }
        }
    }

    private enum FieldErrorMapper implements Function<FieldError, Violation> {
        INSTANCE {
            @Override
            public Violation apply(FieldError fieldError) {
                return Violation.builder()
                        .invalidValue(fieldError.getRejectedValue())
                        .property(fieldError.getField())
                        .type(getType(fieldError))
                        .message(fieldError.getDefaultMessage())
                        .build();
            }
        }
    }

    private static String getType(ObjectError objectError) {
        return Arrays.stream(objectError.getCodes())
                .filter(s -> !s.contains("."))
                .findFirst()
                .map(type -> CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, type))
                .orElse(null);
    }
}
