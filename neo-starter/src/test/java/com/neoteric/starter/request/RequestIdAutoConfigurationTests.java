package com.neoteric.starter.request;

import com.neoteric.starter.StarterConstants;
import com.neoteric.starter.jersey.JerseyDefaultsAutoConfiguration;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ServerPropertiesAutoConfiguration;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.lang.annotation.*;

import static org.assertj.core.api.Assertions.*;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@SpringApplicationConfiguration(RequestIdAutoConfigurationTests.Application.class)
@IntegrationTest({"server.port=0", "spring.main.banner_mode=off"})
@WebAppConfiguration
public class RequestIdAutoConfigurationTests {

    @Value("${local.server.port}")
    private int port;

    private RestTemplate restTemplate = new TestRestTemplate();

    @Test
    public void isRequestIdAvailableInMDC() {
        ResponseEntity<String> entity = this.restTemplate.getForEntity(
                "http://localhost:" + this.port + "/api/requestId", String.class);
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
