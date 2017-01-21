package eu.neoteric.starter.swagger;

import eu.neoteric.starter.embedded.MockEmbeddedServletContainerFactory;
import eu.neoteric.starter.mvc.StarterMvcAutoConfiguration;
import org.junit.After;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.web.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ErrorMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.web.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ServerPropertiesAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.ConfigurableWebApplicationContext;

import javax.validation.constraints.NotNull;
import java.lang.annotation.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SwaggerAutoConfigurationIntegrationTest {

    private ConfigurableWebApplicationContext wac;

    private MockMvc mockMvc;

    @After
    public void close() {
        if (this.wac != null) {
            this.wac.close();
        }
    }

    @Test
    public void defaultSwaggerEndpointLocation() throws Exception {
        this.wac = (ConfigurableWebApplicationContext) new SpringApplicationBuilder(SwaggerConfiguration.class).run();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        this.mockMvc.perform(get("/swagger")).andExpect(status().isOk());
    }

    @Test
    public void swaggerDisabledOnProperty() throws Exception {
        this.wac = (ConfigurableWebApplicationContext) new SpringApplicationBuilder(SwaggerConfiguration.class)
                .properties("neostarter.swagger.enabled=false")
                .run();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        this.mockMvc.perform(get("/swagger")).andExpect(status().isNotFound());
    }

    @Test
    public void shouldAddApiInfoDetails() throws Exception {
        this.wac = (ConfigurableWebApplicationContext) new SpringApplicationBuilder(SwaggerConfiguration.class)
                .properties("neostarter.swagger.license=license",
                        "neostarter.swagger.licenseUrl=licenseUrl",
                        "neostarter.swagger.title=ApiTitle",
                        "neostarter.swagger.description=ApiDescription",
                        "neostarter.swagger.termsOfServiceUrl=termsUrl",
                        "neostarter.swagger.contact.name=contactName",
                        "neostarter.swagger.contact.email=contactEmail",
                        "neostarter.swagger.contact.url=contactUrl",
                        "neostarter.swagger.version=2",
                        "neostarter.swagger.basePackage="
                        )
                .run();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();

        this.mockMvc.perform(get("/swagger"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.license.name").value("license"))
                .andExpect(jsonPath("$.info.license.url").value("licenseUrl"))
                .andExpect(jsonPath("$.info.title").value("ApiTitle"))
                .andExpect(jsonPath("$.info.description").value("ApiDescription"))
                .andExpect(jsonPath("$.info.termsOfService").value("termsUrl"))
                .andExpect(jsonPath("$.info.contact.name").value("contactName"))
                .andExpect(jsonPath("$.info.contact.email").value("contactEmail"))
                .andExpect(jsonPath("$.info.contact.url").value("contactUrl"))
                .andExpect(jsonPath("$.info.version").value("2"));
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Import({ServerPropertiesAutoConfiguration.class,
            DispatcherServletAutoConfiguration.class, StarterMvcAutoConfiguration.class,
            HttpMessageConvertersAutoConfiguration.class, ErrorMvcAutoConfiguration.class,
            PropertyPlaceholderAutoConfiguration.class})
    protected @interface MinimalWebConfiguration {
    }

    @Configuration
    @MinimalWebConfiguration
    @Import(SwaggerAutoConfiguration.class)
    @RestController
    public static class SwaggerConfiguration {

        @Bean
        public MockEmbeddedServletContainerFactory embeddedServletContainerFactory() {
            return new MockEmbeddedServletContainerFactory();
        }

        // For manual testing
        public static void main(String[] args) {
            SpringApplication.run(TestConfiguration.class, args);
        }


        @GetMapping("/hello/{name}")
        public String hello(@NotNull @PathVariable String name) {
            return "hello " + name;
        }

    }
}