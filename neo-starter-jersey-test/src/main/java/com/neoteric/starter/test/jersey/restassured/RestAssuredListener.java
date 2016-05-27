package com.neoteric.starter.test.jersey.restassured;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.config.ObjectMapperConfig;
import com.neoteric.starter.test.jersey.StarterTestConstants;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

public class RestAssuredListener extends AbstractTestExecutionListener {

    @Override
    public void beforeTestClass(TestContext testContext) throws Exception {
        if (AnnotationUtils.findAnnotation(testContext.getTestClass(), ContainerIntegrationTest.class) == null) {
            return;
        }
        ApplicationContext applicationContext = testContext.getApplicationContext();
        String property = applicationContext.getEnvironment().getProperty(StarterTestConstants.LOCAL_SERVER_PORT);
        RestAssured.port = Integer.parseInt(property);

        ObjectMapper objectMapper = applicationContext.getBean(StarterTestConstants.JACKSON_OBJECT_MAPPER_BEAN, ObjectMapper.class);

        RestAssured.config = RestAssured.config().objectMapperConfig(
                new ObjectMapperConfig().jackson2ObjectMapperFactory((cls, charset) -> objectMapper));
    }

    @Override
    public int getOrder() {
        return 800;
    }
}