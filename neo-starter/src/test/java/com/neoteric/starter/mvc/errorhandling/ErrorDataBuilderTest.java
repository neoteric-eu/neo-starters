package com.neoteric.starter.mvc.errorhandling;

import com.google.common.collect.ImmutableMap;
import com.neoteric.starter.StarterConstants;
import com.neoteric.starter.mvc.errorhandling.handler.ExceptionHandlerBinding;
import com.neoteric.starter.mvc.errorhandling.handler.RestExceptionHandler;
import com.neoteric.starter.mvc.errorhandling.resolver.ErrorData;
import com.neoteric.starter.mvc.errorhandling.resolver.ErrorDataBuilder;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.jboss.logging.MDC;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@RunWith(JUnitParamsRunner.class)
public class ErrorDataBuilderTest {

    private static final Clock FIXED_CLOCK = Clock.fixed(Instant.parse("2010-01-10T10:00:00Z"), ZoneId.of("UTC"));
    private static final String MESSAGE = "Wrong argument";
    private static final String REQUEST_URI = "/api/endpoint";
    private static final String REQUEST_ID = "RequestId";
    private static final ErrorProperties STACKTRACE_ALWAYS = new ErrorProperties();
    private static final ErrorProperties STACKTRACE_NEVER = new ErrorProperties();
    private static final ErrorProperties STACKTRACE_PARAM = new ErrorProperties();
    private static final boolean SUPPRESS_STACKTRACE = true;
    private static final boolean DONT_SUPPRESS_STACKTRACE = false;
    private static final boolean STACKTRACE_NOT_POPULATED = false;
    private static final boolean STACKTRACE_POPULATED = true;
    private static final MockHttpServletRequest REQUEST = new MockHttpServletRequest(null, REQUEST_URI);
    private static final MockHttpServletRequest REQUEST_TRACE_PARAM = new MockHttpServletRequest(null, REQUEST_URI);
    public static final String BAD_CODE = "BAD_CODE";

    static {
        STACKTRACE_ALWAYS.setIncludeStacktrace(ErrorProperties.IncludeStacktrace.ALWAYS);
        STACKTRACE_NEVER.setIncludeStacktrace(ErrorProperties.IncludeStacktrace.NEVER);
        STACKTRACE_PARAM.setIncludeStacktrace(ErrorProperties.IncludeStacktrace.ON_TRACE_PARAM);
        REQUEST_TRACE_PARAM.addParameter("trace", "true");
    }

    @Mock
    private ServerProperties serverProperties;
    @Mock
    private ExceptionHandlerBinding binding;

    private ErrorDataBuilder builder;
    private RestExceptionHandler handler;


    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        builder = new ErrorDataBuilder(FIXED_CLOCK, serverProperties);
        handler = new IllegalArgumentExceptionHandler();
        MDC.put(StarterConstants.REQUEST_ID_HEADER, REQUEST_ID);
    }

    @After
    public void resetMDC() {
        MDC.remove(StarterConstants.REQUEST_ID_HEADER);
    }

    @Test
    public void shouldBuildCompleteData() throws Exception {
        mockDefaultIllegalArgumentException();
        ErrorData errorData = builder.build(handler, binding, REQUEST, new IllegalArgumentException(MESSAGE));

        assertThat(errorData.getMessage()).isEqualTo(MESSAGE);
        assertThat(errorData.getPath()).isEqualTo(REQUEST_URI);
        assertThat(errorData.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorData.getError()).isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase());
        assertThat(errorData.getException()).isEqualTo(IllegalArgumentException.class.getName());
        assertThat(errorData.getApplicationCode()).isEqualTo(BAD_CODE);
        assertThat(errorData.getTimestamp()).isEqualTo(ZonedDateTime.now(FIXED_CLOCK));
        assertThat(errorData.getRequestId()).isEqualTo(REQUEST_ID);
        assertThat(errorData.getStackTrace()).isNotEmpty();
        assertThat(errorData.getAdditionalInfo())
                .hasSize(2)
                .containsKey("stackTrace")
                .containsEntry("field", "value");
    }

    @Test
    public void shouldNotPopulateException() throws Exception {
        mockDefaultIllegalArgumentException();
        when(binding.isSuppressException()).thenReturn(true);

        ErrorData errorData = builder.build(handler, binding, REQUEST, new IllegalArgumentException(MESSAGE));
        assertThat(errorData.getException()).isNullOrEmpty();
    }

    private void mockDefaultIllegalArgumentException() {
        when(serverProperties.getError()).thenReturn(STACKTRACE_ALWAYS);
        when(binding.getExceptionClass()).thenAnswer(x -> IllegalArgumentException.class);
        when(binding.getHttpStatus()).thenReturn(HttpStatus.BAD_REQUEST);
        when(binding.getApplicationCode()).thenReturn(BAD_CODE);
        when(binding.isSuppressException()).thenReturn(false);
        when(binding.isSuppressStacktrace()).thenReturn(false);
    }

    @Test
    @Parameters(method = "stacktrace")
    public void shouldPopulateStackTrace(ErrorProperties errorProps, HttpServletRequest request,
                                         boolean suppressStacktrace, boolean stacktracePopulated) throws Exception {
        when(serverProperties.getError()).thenReturn(errorProps);
        when(binding.getExceptionClass()).thenAnswer(x -> IllegalArgumentException.class);
        when(binding.getHttpStatus()).thenReturn(HttpStatus.BAD_REQUEST);
        when(binding.isSuppressException()).thenReturn(false);
        when(binding.isSuppressStacktrace()).thenReturn(suppressStacktrace);

        ErrorData errorData = builder.build(handler, binding, request, new IllegalArgumentException(MESSAGE));
        assertThat(errorData.getStackTrace() != null && errorData.getStackTrace().size() > 0).isEqualTo(stacktracePopulated);
    }

    private Object[] stacktrace() {
        return new Object[]{
                new Object[]{STACKTRACE_ALWAYS, REQUEST, SUPPRESS_STACKTRACE, STACKTRACE_NOT_POPULATED},
                new Object[]{STACKTRACE_ALWAYS, REQUEST, DONT_SUPPRESS_STACKTRACE, STACKTRACE_POPULATED},
                new Object[]{STACKTRACE_NEVER, REQUEST, SUPPRESS_STACKTRACE, STACKTRACE_NOT_POPULATED},
                new Object[]{STACKTRACE_NEVER, REQUEST, DONT_SUPPRESS_STACKTRACE, STACKTRACE_NOT_POPULATED},
                new Object[]{STACKTRACE_PARAM, REQUEST, DONT_SUPPRESS_STACKTRACE, STACKTRACE_NOT_POPULATED},
                new Object[]{STACKTRACE_PARAM, REQUEST_TRACE_PARAM, DONT_SUPPRESS_STACKTRACE, STACKTRACE_POPULATED}
        };
    }

    static class IllegalArgumentExceptionHandler implements RestExceptionHandler<IllegalArgumentException> {

        @Override
        public Object errorMessage(IllegalArgumentException exception, HttpServletRequest request) {
            return exception.getMessage();
        }

        @Override
        public Map<String, Object> additionalInfo(IllegalArgumentException exception, HttpServletRequest request) {
            return ImmutableMap.of("field", "value");
        }
    }
}