package com.neoteric.starter.test.restassured;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.config.ObjectMapperConfig;
import com.neoteric.starter.test.SpringBootEmbeddedTest;
import com.neoteric.starter.test.utils.TestContextHelper;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import static com.neoteric.starter.test.StarterTestConstants.JACKSON_OBJECT_MAPPER_BEAN;
import static com.neoteric.starter.test.StarterTestConstants.LOCAL_SERVER_PORT;

@Order(800) //TODO: Check if still necessary
public class RestAssuredEmbeddedListener extends AbstractTestExecutionListener {

    @Override
    public void beforeTestClass(TestContext testContext) throws Exception {
        TestContextHelper contextHelper = new TestContextHelper(testContext);
        if (contextHelper.testClassAnnotationNotPresent(SpringBootEmbeddedTest.class)) {
            return;
        }

        RestAssured.port = Integer.parseInt(contextHelper.getProperty(LOCAL_SERVER_PORT));
        ObjectMapper objectMapper = contextHelper.getBean(JACKSON_OBJECT_MAPPER_BEAN, ObjectMapper.class);

        RestAssured.config = RestAssured.config().objectMapperConfig(
                new ObjectMapperConfig().jackson2ObjectMapperFactory((cls, charset) -> objectMapper));
    }

    @Override
    public void afterTestClass(TestContext testContext) throws Exception {
        RestAssured.reset();
    }
}