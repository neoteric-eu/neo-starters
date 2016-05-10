package com.neoteric.starter.saasmgr.client.feign;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.neoteric.starter.saasmgr.FeignClientAutoConfiguration;
import com.neoteric.starter.saasmgr.client.SaasMgrClient;
import com.netflix.config.ConfigurationManager;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ServerList;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.cloud.netflix.archaius.ArchaiusAutoConfiguration;
import org.springframework.cloud.netflix.feign.FeignAutoConfiguration;
import org.springframework.cloud.netflix.feign.ribbon.FeignRibbonClientAutoConfiguration;
import org.springframework.cloud.netflix.ribbon.RibbonAutoConfiguration;
import org.springframework.cloud.netflix.ribbon.StaticServerList;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

public class FeignSaasMgrClientTest {

    private AnnotationConfigApplicationContext context;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort());

    @Before
    public void setUp() {
        load("neostarter.saasmgr.feign.name=saasManager",
                "saasManager.ribbon.listOfServers=localhost:" + wireMockRule.port());
    }

    @After
    public void close() {
        if (this.context != null) {
            this.context.close();
        }
    }

    private void load(String... environment) {
        this.context = new AnnotationConfigApplicationContext();
        EnvironmentTestUtils.addEnvironment(this.context, environment);
        this.context.register(
                FeignClientAutoConfiguration.class,
                FeignRibbonClientAutoConfiguration.class,
                ArchaiusAutoConfiguration.class,
                HttpMessageConvertersAutoConfiguration.class,
                RibbonAutoConfiguration.class,
                FeignAutoConfiguration.class);
        this.context.refresh();
    }

    @Test
    public void getLoginInfo() throws Exception {

        stubFor(get(urlEqualTo("/api/v1/users/authToken"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("authToken.json")));

        SaasMgrClient feignSaasMgrClient = context.getBean(SaasMgrClient.class);
        System.out.println(feignSaasMgrClient.getLoginInfo("abc", "xyz"));
    }

}