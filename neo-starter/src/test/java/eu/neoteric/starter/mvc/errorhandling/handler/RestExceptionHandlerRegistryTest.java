package eu.neoteric.starter.mvc.errorhandling.handler;

import com.google.common.collect.Sets;
import eu.neoteric.starter.mvc.errorhandling.handler.ExceptionHandlerBinding;
import eu.neoteric.starter.mvc.errorhandling.handler.RestExceptionHandler;
import eu.neoteric.starter.mvc.errorhandling.handler.RestExceptionHandlerProvider;
import eu.neoteric.starter.mvc.errorhandling.handler.RestExceptionHandlerRegistry;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

public class RestExceptionHandlerRegistryTest {

    private RestExceptionHandlerRegistry registry;

    @Test
    public void shouldReturnEmpty_IfBindingsNull() throws Exception {
        registry = new RestExceptionHandlerRegistry(null);
        Optional<ExceptionHandlerBinding> binding = registry.findBindingFor(IllegalStateException.class);
        assertThat(binding).isEmpty();
    }

    @Test
    public void shouldReturnEmpty_IfBindingsEmpty() throws Exception {
        registry = new RestExceptionHandlerRegistry(Sets.newHashSet());
        Optional<ExceptionHandlerBinding> binding = registry.findBindingFor(IllegalStateException.class);
        assertThat(binding).isEmpty();
    }

    @Test
    public void shouldReturnEmpty_IfBindingNotFound() throws Exception {
        ExceptionHandlerBinding binding = ExceptionHandlerBinding.fromAnnotatedClass(IllegalStateExceptionHandler.class);
        registry = new RestExceptionHandlerRegistry(Sets.newHashSet(binding));
        Optional<ExceptionHandlerBinding> foundBinding = registry.findBindingFor(IllegalArgumentException.class);
        assertThat(foundBinding).isEmpty();
    }

    @Test
    public void shouldReturnDirectMatchBinding() throws Exception {
        ExceptionHandlerBinding binding = ExceptionHandlerBinding.fromAnnotatedClass(IllegalStateExceptionHandler.class);
        registry = new RestExceptionHandlerRegistry(Sets.newHashSet(binding));
        Optional<ExceptionHandlerBinding> foundBinding = registry.findBindingFor(IllegalStateException.class);
        assertThat(foundBinding).isPresent();
    }

    @Test
    public void shouldReturnDescendantMatchBinding() throws Exception {
        ExceptionHandlerBinding binding = ExceptionHandlerBinding.fromAnnotatedClass(IllegalStateExceptionHandler.class);
        registry = new RestExceptionHandlerRegistry(Sets.newHashSet(binding));
        Optional<ExceptionHandlerBinding> foundBinding = registry.findBindingFor(IllegalComponentStateException.class);
        assertThat(foundBinding).isPresent();
    }
    @Test
    public void shouldReturnCloserMatchBinding() throws Exception {
        ExceptionHandlerBinding illegalStateBinding = ExceptionHandlerBinding.fromAnnotatedClass(IllegalStateExceptionHandler.class);
        ExceptionHandlerBinding runtimeBinding = ExceptionHandlerBinding.fromAnnotatedClass(RuntimeExceptionHandler.class);
        registry = new RestExceptionHandlerRegistry(Sets.newHashSet(illegalStateBinding, runtimeBinding));
        Optional<ExceptionHandlerBinding> foundBinding = registry.findBindingFor(IllegalComponentStateException.class);
        assertThat(foundBinding)
                .isPresent()
                .contains(illegalStateBinding);
    }

    @RestExceptionHandlerProvider
    private static class IllegalStateExceptionHandler implements RestExceptionHandler<IllegalStateException> {

        @Override
        public Object errorMessage(IllegalStateException exception, HttpServletRequest request) {
            return "message";
        }
    }

    @RestExceptionHandlerProvider
    private static class RuntimeExceptionHandler implements RestExceptionHandler<RuntimeException> {

        @Override
        public Object errorMessage(RuntimeException exception, HttpServletRequest request) {
            return "runtimeExceptionHandler";
        }
    }
}