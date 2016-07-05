package com.neoteric.starter.mvc.logging;

import com.neoteric.starter.feign.CustomFeignProperties;
import com.neoteric.starter.jackson.model.JsonApiList;
import com.neoteric.starter.jackson.model.JsonApiObject;
import com.neoteric.starter.mvc.ApiController;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import static humanize.Humanize.capitalize;
import static humanize.Humanize.decamelize;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ApiLoggingAspectTest {

    @Mock
    private ProceedingJoinPoint point;
    @Mock
    private ApiController apiController;
    @Mock
    private MethodSignature methodSignature;

    @InjectMocks
    private ApiLoggingAspect loggingAspect = new ApiLoggingAspect(new ApiLoggingProperties());

    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();

    @Test
    public void singleStringParam() throws Throwable {
        setUp();
        when(point.getArgs()).thenReturn(new Object[]{"asd"});
        when(methodSignature.getName()).thenReturn("findById");
        when(methodSignature.getParameterNames()).thenReturn(new String[]{"id"});
        when(methodSignature.getParameterTypes()).thenReturn(new Class[]{String.class});

        loggingAspect.around(point, apiController);

        assertLogStatements(
                "INFO com.neoteric.starter.feign.CustomFeignProperties - [JobOffer] Find by id [id: asd].",
                "INFO com.neoteric.starter.feign.CustomFeignProperties - [JobOffer] Find by id [id: asd] took");
    }

    @Test
    public void singleStringParamWithoutResource() throws Throwable {
        setUp();
        when(apiController.resourceName()).thenReturn("");
        when(point.getArgs()).thenReturn(new Object[]{"asd"});
        when(methodSignature.getName()).thenReturn("findById");
        when(methodSignature.getParameterNames()).thenReturn(new String[]{"id"});
        when(methodSignature.getParameterTypes()).thenReturn(new Class[]{String.class});

        loggingAspect.around(point, apiController);

        assertLogStatements(
                "INFO com.neoteric.starter.feign.CustomFeignProperties - Find by id [id: asd].",
                "INFO com.neoteric.starter.feign.CustomFeignProperties - Find by id [id: asd] took");
    }

    @Test
    public void multipleStandardJavaParams() throws Throwable {
        setUp();
        when(point.getArgs()).thenReturn(new Object[]{ZonedDateTime.parse("2010-01-10T10:00Z"), Integer.valueOf(2010)});
        when(methodSignature.getName()).thenReturn("searchWithParams");
        when(methodSignature.getParameterNames()).thenReturn(new String[]{"dateTime", "number"});
        when(methodSignature.getParameterTypes()).thenReturn(new Class[]{ZonedDateTime.class, Integer.TYPE});

        loggingAspect.around(point, apiController);

        assertLogStatements("[JobOffer] Search with params [dateTime: 2010-01-10T10:00Z, number: 2010]");
    }

    @Test
    public void singleCustomParam() throws Throwable {
        setUp();
        when(point.getArgs()).thenReturn(new Object[]{Duty.newBuilder().setName("abc").setValue(5).build()});
        when(methodSignature.getName()).thenReturn("create");
        when(methodSignature.getParameterNames()).thenReturn(new String[]{"duty"});
        when(methodSignature.getParameterTypes()).thenReturn(new Class[]{Duty.class});

        loggingAspect.around(point, apiController);

        assertLogStatements(
                "INFO com.neoteric.starter.feign.CustomFeignProperties - [JobOffer] Create.",
                "DEBUG com.neoteric.starter.feign.CustomFeignProperties - [JobOffer] Details: [duty: Duty(name=abc, value=5, cachedHashCode=2987940)].",
                "INFO com.neoteric.starter.feign.CustomFeignProperties - [JobOffer] Create took");
    }

    @Test
    public void jsonApiListReturned() throws Throwable {
        setUp();
        when(point.getArgs()).thenReturn(new Object[]{});
        when(methodSignature.getName()).thenReturn("find");
        when(methodSignature.getParameterNames()).thenReturn(new String[]{});
        when(methodSignature.getParameterTypes()).thenReturn(new Class[]{});
        when(point.proceed()).thenReturn(JsonApiList.wrap(Duty.newBuilder().setName("abc").setValue(5).build()).build());

        loggingAspect.around(point, apiController);

        assertLogStatements(
                "INFO com.neoteric.starter.feign.CustomFeignProperties - [JobOffer] Find.",
                "INFO com.neoteric.starter.feign.CustomFeignProperties - [JobOffer] Returning 1 item",
                "INFO com.neoteric.starter.feign.CustomFeignProperties - [JobOffer] Find took");
    }

    @Test
    public void jsonApiObjectReturned() throws Throwable {
        setUp();
        when(point.getArgs()).thenReturn(new Object[]{});
        when(methodSignature.getName()).thenReturn("findById");
        when(methodSignature.getParameterNames()).thenReturn(new String[]{});
        when(methodSignature.getParameterTypes()).thenReturn(new Class[]{});
        when(point.proceed()).thenReturn(JsonApiObject.wrap(Duty.newBuilder().setName("abc").setValue(5).build()).build());

        loggingAspect.around(point, apiController);

        assertLogStatements(
                "INFO com.neoteric.starter.feign.CustomFeignProperties - [JobOffer] Find by id.",
                "DEBUG com.neoteric.starter.feign.CustomFeignProperties - [JobOffer] Returning [Duty(",
                "INFO com.neoteric.starter.feign.CustomFeignProperties - [JobOffer] Find by id took");
    }

    @Test
    public void multipleCustomParam() throws Throwable {
        setUp();
        when(point.getArgs()).thenReturn(new Object[]{Duty.newBuilder().setName("abc").setValue(5).build(),
                Duty.newBuilder().setName("xxx").setValue(10).build()});
        when(methodSignature.getName()).thenReturn("create");
        when(methodSignature.getParameterNames()).thenReturn(new String[]{"duty", "duty2"});
        when(methodSignature.getParameterTypes()).thenReturn(new Class[]{Duty.class, Duty.class});

        loggingAspect.around(point, apiController);

        assertLogStatements(
                "INFO com.neoteric.starter.feign.CustomFeignProperties - [JobOffer] Create.",
                "Details: [duty: Duty(name=abc, value=5, cachedHashCode=2987940), duty2: Duty(name=xxx, value=10, cachedHashCode=3694931)]",
                "INFO com.neoteric.starter.feign.CustomFeignProperties - [JobOffer] Create took");
    }

    private Object assertLogStatements(String... statements) {
        return assertThat(systemOutRule.getLog()).contains(statements);
    }

    private void setUp() {
        when(apiController.resourceName()).thenReturn("JobOffer");
        CustomFeignProperties loggerClass = new CustomFeignProperties();
        when(point.getTarget()).thenReturn(loggerClass);
        when(point.getSignature()).thenReturn(methodSignature);
    }
}