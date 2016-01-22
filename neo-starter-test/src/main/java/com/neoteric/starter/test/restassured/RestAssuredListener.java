package com.neoteric.starter.test.restassured;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.config.ObjectMapperConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

public class RestAssuredListener extends AbstractTestExecutionListener {

    @Override
    public void beforeTestClass(TestContext testContext) throws Exception {
        if (testContext.getTestClass().getAnnotation(ContainerIntegrationTest.class) == null) {
            return;
        }

        ApplicationContext applicationContext = testContext.getApplicationContext();
        String property = applicationContext.getEnvironment().getProperty("local.server.port");
        RestAssured.port = Integer.parseInt(property);

        ObjectMapper objectMapper = applicationContext.getBean("jacksonObjectMapper", ObjectMapper.class);

        RestAssured.config = RestAssured.config().objectMapperConfig(
                new ObjectMapperConfig().jackson2ObjectMapperFactory((cls, charset) -> objectMapper));
    }
}