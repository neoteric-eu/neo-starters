package com.neoteric.starter.test.restassured;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.config.ObjectMapperConfig;
import com.jayway.restassured.module.mockmvc.RestAssuredMockMvc;
import com.neoteric.starter.test.SpringBootMockMvcTest;
import com.neoteric.starter.test.utils.TestContextHelper;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.springframework.web.context.WebApplicationContext;

import static com.neoteric.starter.test.StarterTestConstants.JACKSON_OBJECT_MAPPER_BEAN;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@Order(800) //TODO: Check if still necessary
public class RestAssuredMockMvcListener extends AbstractTestExecutionListener {

    @Override
    public void beforeTestClass(TestContext testContext) throws Exception {
        TestContextHelper contextHelper = new TestContextHelper(testContext);
        SpringBootMockMvcTest annotation = contextHelper.getTestClassAnnotation(SpringBootMockMvcTest.class);
        if (annotation == null) {
            return;
        }

        RestAssuredMockMvc.webAppContextSetup((WebApplicationContext) testContext.getApplicationContext());
        ObjectMapper objectMapper = contextHelper.getBean(JACKSON_OBJECT_MAPPER_BEAN, ObjectMapper.class);
        RestAssuredMockMvc.config = RestAssuredMockMvc.config().objectMapperConfig(
                new ObjectMapperConfig().jackson2ObjectMapperFactory((cls, charset) -> objectMapper));

        if (annotation.print()) {
            RestAssuredMockMvc.resultHandlers(print());
        }
    }

    @Override
    public void afterTestClass(TestContext testContext) throws Exception {
        RestAssuredMockMvc.reset();
    }
}