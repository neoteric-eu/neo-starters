package com.neoteric.starter.request;

import com.neoteric.starter.jersey.StarterConstants;
import com.neoteric.starter.jersey.jaxrs.JerseyDefaultsAutoConfiguration;
import com.neoteric.starter.jersey.request.RequestIdAutoConfiguration;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ServerPropertiesAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.lang.annotation.*;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RequestIdAutoConfigurationTests.Application.class, webEnvironment = RANDOM_PORT)
public class RequestIdAutoConfigurationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void isRequestIdAvailableInMDC() {
        ResponseEntity<String> entity = this.restTemplate.getForEntity("/api/requestId", String.class);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(entity.getBody()).isNotNull();
        assertThat(entity.getHeaders().getFirst(StarterConstants.REQUEST_ID)).isNotNull();
    }

    @MinimalWebConfiguration
    @Import(RequestIdAutoConfiguration.class)
    @Path("/requestId")
    public static class Application extends ResourceConfig {

        @GET
        public String getRequestId() {
            return MDC.get(StarterConstants.REQUEST_ID);
        }

        public Application() {
            register(Application.class);
        }

        public static void main(String[] args) {
            SpringApplication.run(Application.class, args);
        }

    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Import({ EmbeddedServletContainerAutoConfiguration.class,
            ServerPropertiesAutoConfiguration.class, JerseyDefaultsAutoConfiguration.class,
            PropertyPlaceholderAutoConfiguration.class })
    protected @interface MinimalWebConfiguration {
    }
}
