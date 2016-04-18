package com.neoteric.starter.test.restassured;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.config.ObjectMapperConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import static com.neoteric.starter.test.StarterTestConstants.JACKSON_OBJECT_MAPPER_BEAN;
import static com.neoteric.starter.test.StarterTestConstants.LOCAL_SERVER_PORT;

public class RestAssuredListener extends AbstractTestExecutionListener {

    @Override
    public void beforeTestClass(TestContext testContext) throws Exception {
        if (AnnotationUtils.findAnnotation(testContext.getTestClass(), ContainerIntegrationTest.class) == null) {
            return;
        }
        ApplicationContext applicationContext = testContext.getApplicationContext();
        String property = applicationContext.getEnvironment().getProperty(LOCAL_SERVER_PORT);
        RestAssured.port = Integer.parseInt(property);

        ObjectMapper objectMapper = applicationContext.getBean(JACKSON_OBJECT_MAPPER_BEAN, ObjectMapper.class);

        RestAssured.config = RestAssured.config().objectMapperConfig(
                new ObjectMapperConfig().jackson2ObjectMapperFactory((cls, charset) -> objectMapper));
    }

    @Override
    public int getOrder() {
        return 800;
    }
}