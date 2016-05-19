package com.neoteric.starter.saasmgr.client.feign;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.neoteric.starter.saasmgr.FeignClientAutoConfiguration;
import com.neoteric.starter.saasmgr.client.SaasMgrClient;
import com.neoteric.starter.saasmgr.client.SaasMgrClientTestHelper;
import com.neoteric.starter.saasmgr.model.LoginData;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.boot.autoconfigure.web.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.cloud.netflix.archaius.ArchaiusAutoConfiguration;
import org.springframework.cloud.netflix.feign.FeignAutoConfiguration;
import org.springframework.cloud.netflix.feign.ribbon.FeignRibbonClientAutoConfiguration;
import org.springframework.cloud.netflix.ribbon.RibbonAutoConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

public class FeignSaasMgrClientTest extends SaasMgrClientTestHelper {

    private AnnotationConfigApplicationContext context;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort());

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

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
    public void shouldGetAllLoginInformation() throws Exception {
        stubForCompleteResponse();
        SaasMgrClient feignSaasMgrClient = context.getBean(SaasMgrClient.class);
        LoginData loginData = feignSaasMgrClient.getLoginInfo("xxx", "xxx");
        assertCompleteData(loginData);
    }

    @Test
    public void shouldThrowBadCredentialsWhen401StatusReturned() throws Exception {
        stubForUnauthorized();
        expectedException.expect(BadCredentialsException.class);
        SaasMgrClient feignSaasMgrClient = context.getBean(SaasMgrClient.class);
        feignSaasMgrClient.getLoginInfo("xxx", "xxx");
    }

    @Test
    public void shouldThrowAuthenticationServiceExceptionWhenNot200StatusReturned() throws Exception {
        stubForServiceUnavailable();
        expectedException.expect(AuthenticationServiceException.class);
        SaasMgrClient feignSaasMgrClient = context.getBean(SaasMgrClient.class);
        feignSaasMgrClient.getLoginInfo("xxx", "xxx");
    }
}