package com.neoteric.starter.mvc.errorhandling;

import com.google.common.collect.ImmutableMap;
import com.neoteric.starter.clock.TimeZoneAutoConfiguration;
import com.neoteric.starter.embedded.MockEmbeddedServletContainerFactory;
import com.neoteric.starter.jackson.StarterJacksonBeforeAutoConfiguration;
import com.neoteric.starter.mvc.StarterMvcAutoConfiguration;
import com.neoteric.starter.mvc.errorhandling.handler.RestExceptionHandler;
import com.neoteric.starter.mvc.errorhandling.handlers.common.FallbackExceptionHandler;
import com.neoteric.starter.mvc.errorhandling.resolver.RestExceptionResolver;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.web.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ErrorMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.web.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ServerPropertiesAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.util.NestedServletException;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.*;
import java.util.Map;

import static org.hamcrest.core.Is.isA;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class StarterErrorHandlingAutoConfigurationTest {

    private ConfigurableWebApplicationContext wac;

    private MockMvc mockMvc;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @After
    public void close() {
        if (this.wac != null) {
            this.wac.close();
        }
    }

    @Test
    public void shouldNotUseRestExceptionHandlingOnDisabledProperty() throws Exception {
        this.wac = (ConfigurableWebApplicationContext) new SpringApplicationBuilder(SwaggerConfiguration.class)
                .properties("neostarter.mvc.restErrorHandling.enabled=false").run();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        expectedException.expect(NestedServletException.class);
        this.mockMvc.perform(get("/hello"));
    }

    @Test
    public void shouldUseFallbackExceptionHandler() throws Exception {
        this.wac = (ConfigurableWebApplicationContext) new SpringApplicationBuilder(SwaggerConfiguration.class).run();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        MvcResult mvcResult = this.mockMvc.perform(get("/hello"))
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.path").value("/hello"))
                .andExpect(jsonPath("$.message").value(FallbackExceptionHandler.FALLBACK_ERROR_MSG))
                .andExpect(jsonPath("$.status").value(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .andExpect(jsonPath("$.error").value(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())).andReturn();

        System.out.println(mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void testWithoutAnyHandlers_ExceptionShouldOccur() throws Exception {
        this.wac = (ConfigurableWebApplicationContext) new SpringApplicationBuilder(SwaggerConfiguration.class)
                .properties("neostarter.mvc.restErrorHandling.defaultHandlersEnabled=false").run();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        expectedException.expectCause(isA(RestExceptionResolver.NoExceptionHandlerFoundException.class));
        this.mockMvc.perform(get("/hello"));
    }

    @Test
    public void WithDefaultHandlersIShouldBeAbleToOverrideByAScannedOne() throws Exception {
        this.wac = (ConfigurableWebApplicationContext) new SpringApplicationBuilder(SwaggerConfiguration.class).run();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        MvcResult mvcResult = this.mockMvc.perform(get("/hello")).andReturn();
        System.out.println(mvcResult.getResponse().getContentAsString());
    }


    @Test
    public void StackTraceShouldAppear() throws Exception {
        //TODO
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Import({StarterJacksonBeforeAutoConfiguration.class, JacksonAutoConfiguration.class,StarterErrorHandlingAutoConfiguration.class,
            ServerPropertiesAutoConfiguration.class, DispatcherServletAutoConfiguration.class, TimeZoneAutoConfiguration.class,
            ExceptionHandlersRegistryAutoConfiguration.class, ScannedExceptionHandlersAutoConfiguration.class,
            DefaultExceptionHandlersAutoConfiguration.class, SecurityExceptionHandlersAutoConfiguration.class,
            StarterMvcAutoConfiguration.class, HttpMessageConvertersAutoConfiguration.class, ErrorMvcAutoConfiguration.class,
            PropertyPlaceholderAutoConfiguration.class})
    @AutoConfigurationPackage
    protected @interface MinimalWebConfiguration {
    }

    @Configuration
    @MinimalWebConfiguration
    @RestController
    public static class SwaggerConfiguration {

        @Bean
        public MockEmbeddedServletContainerFactory embeddedServletContainerFactory() {
            return new MockEmbeddedServletContainerFactory();
        }

        public static void main(String[] args) {
            SpringApplication.run(TestConfiguration.class, args);
        }


        @GetMapping("/hello")
        public String hello() {
            throw new RuntimeException("eao");
        }
    }

    @RestController
    static class ExceptionHandler implements RestExceptionHandler<RuntimeException>{

        @Override
        public Object errorMessage(RuntimeException exception, HttpServletRequest request) {
            return "error";
        }

        @Override
        public Map<String, Object> additionalInfo(RuntimeException exception, HttpServletRequest request) {
            return ImmutableMap.of("path", "overriddenPath");
        }
    }
}