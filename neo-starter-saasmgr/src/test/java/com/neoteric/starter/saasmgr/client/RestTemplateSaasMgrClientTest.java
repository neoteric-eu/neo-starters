package com.neoteric.starter.saasmgr.client;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.neoteric.starter.saasmgr.RestTemplateClientAutoConfiguration;
import com.neoteric.starter.saasmgr.model.LoginData;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

public class RestTemplateSaasMgrClientTest extends SaasMgrClientTestHelper {

    private AnnotationConfigApplicationContext context;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort());

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        load("neostarter.saasmgr.address=http://localhost:" + wireMockRule.port());
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
        this.context.register(RestTemplateClientAutoConfiguration.class);
        this.context.refresh();
    }

    @Test
    public void shouldGetAllLoginInformation() throws Exception {
        stubForCompleteResponse();
        SaasMgrClient restTemplateClient = context.getBean(SaasMgrClient.class);
        LoginData loginData = restTemplateClient.getLoginInfo("xxx", "xxx");
        assertCompleteData(loginData);
    }

    @Test
    public void shouldThrowBadCredentialsWhen401StatusReturned() throws Exception {
        stubForUnauthorized();
        expectedException.expect(BadCredentialsException.class);
        SaasMgrClient restTemplateClient = context.getBean(SaasMgrClient.class);
        restTemplateClient.getLoginInfo("xxx", "xxx");
    }

    @Test
    public void shouldThrowAuthenticationServiceExceptionWhenNot200StatusReturned() throws Exception {
        stubForServiceUnavailable();
        expectedException.expect(AuthenticationServiceException.class);
        SaasMgrClient restTemplateClient = context.getBean(SaasMgrClient.class);
        restTemplateClient.getLoginInfo("xxx", "xxx");
    }
}