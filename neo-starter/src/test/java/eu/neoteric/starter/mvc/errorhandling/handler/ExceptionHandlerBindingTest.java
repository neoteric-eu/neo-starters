package eu.neoteric.starter.mvc.errorhandling.handler;

import com.google.common.collect.Sets;
import eu.neoteric.starter.mvc.errorhandling.handler.ExceptionHandlerBinding;
import eu.neoteric.starter.mvc.errorhandling.handler.RestExceptionHandler;
import eu.neoteric.starter.mvc.errorhandling.handler.RestExceptionHandlerProvider;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


public class ExceptionHandlerBindingTest {

    private static final String MESSAGE = "message";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldThrowException_WhenNullProvided() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        ExceptionHandlerBinding.fromAnnotatedClass(null);
    }

    @Test
    public void shouldThrowException_WhenNotImplementingRestExceptionProvider() throws Exception {
        expectedException.expect(IllegalStateException.class);
        ExceptionHandlerBinding.fromAnnotatedClass(ExceptionProviderNotImplementing.class);
    }

    @Test
    public void shouldThrowException_WhenNotRestExceptionProviderAnnotated() throws Exception {
        expectedException.expect(IllegalStateException.class);
        ExceptionHandlerBinding.fromAnnotatedClass(ExceptionProviderNotAnnotated.class);
    }

    @Test
    public void shouldBeEqualOnlyWithExceptionClass() throws Exception {
        EqualsVerifier.forClass(ExceptionHandlerBinding.class)
                .withOnlyTheseFields("exceptionClass")
                .suppress(Warning.STRICT_INHERITANCE)
                .verify();
    }

    @Test
    public void shouldBeUniqueOnExceptionClass() throws Exception {
        ExceptionHandlerBinding binding = ExceptionHandlerBinding.builder()
                .exceptionClass(IllegalStateException.class)
                .exceptionHandlerClass(IllegalStateExceptionExceptionProvider.class)
                .httpStatus(HttpStatus.BAD_REQUEST)
                .logLevel(Level.DEBUG)
                .suppressException(true)
                .suppressStacktrace(true)
                .exceptionHandlerBeanName("name")
                .logger(LoggerFactory.getLogger(IllegalStateExceptionExceptionProvider.class))
                .build();

        ExceptionHandlerBinding binding2 = ExceptionHandlerBinding.builder()
                .exceptionClass(IllegalStateException.class)
                .exceptionHandlerClass(IllegalStateExceptionExceptionNonDefaultsProvider.class)
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .logLevel(Level.ERROR)
                .suppressException(false)
                .suppressStacktrace(false)
                .exceptionHandlerBeanName("name2")
                .logger(LoggerFactory.getLogger(IllegalStateExceptionExceptionNonDefaultsProvider.class))
                .build();

        Set<ExceptionHandlerBinding> bindings = Sets.newHashSet(binding, binding2);
        assertThat(bindings).hasSize(1);

    }

    @Test
    public void shouldBuildBindingWithProperAnnotationDefaults() throws Exception {
        ExceptionHandlerBinding binding = ExceptionHandlerBinding.fromAnnotatedClass(IllegalStateExceptionExceptionProvider.class);
        assertThat(binding.getHttpStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(binding.getLogLevel()).isEqualTo(Level.ERROR);
        assertThat(binding.getExceptionClass()).isEqualTo(IllegalStateException.class);
        assertThat(binding.getCause()).isEmpty();
        assertThat(binding.getLogger()).isEqualTo(LoggerFactory.getLogger(IllegalStateExceptionExceptionProvider.class));
        assertThat(binding.getExceptionHandlerClass()).isEqualTo(IllegalStateExceptionExceptionProvider.class);
        assertThat(binding.isSuppressException()).isFalse();
        assertThat(binding.isSuppressStacktrace()).isFalse();
    }

    @Test
    public void shouldBuildBindingWithProvidedAnnotationValues() throws Exception {
        ExceptionHandlerBinding binding = ExceptionHandlerBinding.fromAnnotatedClass(IllegalStateExceptionExceptionNonDefaultsProvider.class);
        assertThat(binding.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(binding.getLogLevel()).isEqualTo(Level.WARN);
        assertThat(binding.getExceptionClass()).isEqualTo(IllegalStateException.class);
        assertThat(binding.getCause()).isEqualTo("123");
        assertThat(binding.getLogger()).isEqualTo(LoggerFactory.getLogger(IllegalStateExceptionExceptionNonDefaultsProvider.class));
        assertThat(binding.getExceptionHandlerClass()).isEqualTo(IllegalStateExceptionExceptionNonDefaultsProvider.class);
        assertThat(binding.isSuppressException()).isTrue();
        assertThat(binding.isSuppressStacktrace()).isTrue();
    }

    @RestExceptionHandlerProvider(httpStatus = HttpStatus.BAD_REQUEST, logLevel = Level.WARN, applicationCode = "123", suppressException = true, suppressStackTrace = true)
    private static class IllegalStateExceptionExceptionNonDefaultsProvider implements RestExceptionHandler<IllegalStateException> {

        @Override
        public Object errorMessage(IllegalStateException exception, HttpServletRequest request) {
            return MESSAGE;
        }
    }

    @RestExceptionHandlerProvider
    private static class IllegalStateExceptionExceptionProvider implements RestExceptionHandler<IllegalStateException> {

        @Override
        public Object errorMessage(IllegalStateException exception, HttpServletRequest request) {
            return MESSAGE;
        }
    }

    private static class ExceptionProviderNotAnnotated implements RestExceptionHandler<Exception> {
        @Override
        public Object errorMessage(Exception exception, HttpServletRequest request) {
            return "hello";
        }
    }

    @RestExceptionHandlerProvider
    private static class ExceptionProviderNotImplementing {
    }
}