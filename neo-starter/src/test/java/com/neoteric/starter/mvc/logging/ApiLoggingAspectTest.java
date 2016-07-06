package com.neoteric.starter.mvc.logging;

import com.google.common.collect.Lists;
import com.neoteric.starter.feign.CustomFeignProperties;
import com.neoteric.starter.jackson.model.JsonApiList;
import com.neoteric.starter.jackson.model.JsonApiObject;
import com.neoteric.starter.mvc.ApiController;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.event.Level;

import java.time.ZonedDateTime;

import static ch.qos.logback.classic.Level.DEBUG;
import static ch.qos.logback.classic.Level.INFO;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ApiLoggingAspectTest {

    private static ApiLoggingProperties props;

    static {
        props = new ApiLoggingProperties();
        props.setJsonApiObjectLevel(Level.INFO);
        props.setCustomParamsLevel(Level.INFO);
    }

    @Mock
    private ProceedingJoinPoint point;
    @Mock
    private ApiController apiController;
    @Mock
    private MethodSignature methodSignature;

    @InjectMocks
    private ApiLoggingAspect loggingAspect = new ApiLoggingAspect(props);

    @Rule
    public LogbackVerifier logbackVerifier = new LogbackVerifier();

    @Test
    public void singleStringParam() throws Throwable {
        setUp();
        when(point.getArgs()).thenReturn(new Object[]{"asd"});
        when(methodSignature.getName()).thenReturn("findById");
        when(methodSignature.getParameterNames()).thenReturn(new String[]{"id"});
        when(methodSignature.getParameterTypes()).thenReturn(new Class[]{String.class});

        logbackVerifier.expectMessage(INFO, "[JobOffer] Find by id [id: asd].");
        logbackVerifier.expectMessage(INFO, "[JobOffer] Find by id [id: asd] took");

        loggingAspect.around(point, apiController);
    }

    @Test
    public void singleStringParamWithoutResource() throws Throwable {
        setUp();
        when(apiController.resourceName()).thenReturn("");
        when(point.getArgs()).thenReturn(new Object[]{"asd"});
        when(methodSignature.getName()).thenReturn("findById");
        when(methodSignature.getParameterNames()).thenReturn(new String[]{"id"});
        when(methodSignature.getParameterTypes()).thenReturn(new Class[]{String.class});

        //TODO: In fact it's not really checking if resource has not been prepended. It requires so heavy mods in LogbackVerifier
        //TODO: Add verifying logging class
        logbackVerifier.expectMessage(INFO, "Find by id [id: asd].");
        logbackVerifier.expectMessage(INFO, "Find by id [id: asd] took");
        loggingAspect.around(point, apiController);
    }

    @Test
    public void multipleStandardJavaParams() throws Throwable {
        setUp();
        when(point.getArgs()).thenReturn(new Object[]{ZonedDateTime.parse("2010-01-10T10:00Z"), Integer.valueOf(2010)});
        when(methodSignature.getName()).thenReturn("searchWithParams");
        when(methodSignature.getParameterNames()).thenReturn(new String[]{"dateTime", "number"});
        when(methodSignature.getParameterTypes()).thenReturn(new Class[]{ZonedDateTime.class, Integer.TYPE});

        logbackVerifier.expectMessage(INFO, "[JobOffer] Search with params [dateTime: 2010-01-10T10:00Z, number: 2010].");
        loggingAspect.around(point, apiController);
    }

    @Test
    public void mixedParams() throws Throwable {
        setUp();
        when(point.getArgs()).thenReturn(new Object[]{Duty.newBuilder().setName("abc").setValue(5).build(), Integer.valueOf(2010)});
        when(methodSignature.getName()).thenReturn("searchWithParams");
        when(methodSignature.getParameterNames()).thenReturn(new String[]{"duty", "number"});
        when(methodSignature.getParameterTypes()).thenReturn(new Class[]{Duty.class, Integer.TYPE});

        logbackVerifier.expectMessage(INFO, "[JobOffer] Search with params [number: 2010].");
        logbackVerifier.expectMessage(INFO, "[JobOffer] Details: [duty: Duty(name=abc, value=5, cachedHashCode=2987940)].");

        loggingAspect.around(point, apiController);
    }

    @Test
    public void jsonApiListReturnedSingleElement() throws Throwable {
        setUp();
        when(point.getArgs()).thenReturn(new Object[]{});
        when(methodSignature.getName()).thenReturn("find");
        when(methodSignature.getParameterNames()).thenReturn(new String[]{});
        when(methodSignature.getParameterTypes()).thenReturn(new Class[]{});
        when(point.proceed()).thenReturn(JsonApiList.wrap(Duty.newBuilder().setName("abc").setValue(5).build()).build());

        logbackVerifier.expectMessage(INFO, "[JobOffer] Find.");
        logbackVerifier.expectMessage(INFO, "[JobOffer] Returning 1 item.");
        logbackVerifier.expectMessage(INFO, "[JobOffer] Find took");

        loggingAspect.around(point, apiController);
    }

    @Test
    public void jsonApiListReturnedMultipleElements() throws Throwable {
        setUp();
        when(point.getArgs()).thenReturn(new Object[]{});
        when(methodSignature.getName()).thenReturn("find");
        when(methodSignature.getParameterNames()).thenReturn(new String[]{});
        when(methodSignature.getParameterTypes()).thenReturn(new Class[]{});
        when(point.proceed()).thenReturn(JsonApiList.wrap(
                Lists.newArrayList(
                        Duty.newBuilder().setName("abc").setValue(5).build(),
                        Duty.newBuilder().setName("xxx").setValue(50).build()))
                .build());

        logbackVerifier.expectMessage(INFO, "[JobOffer] Returning 2 items");

        loggingAspect.around(point, apiController);
    }

    @Test
    public void jsonApiObjectReturned() throws Throwable {
        setUp();
        when(point.getArgs()).thenReturn(new Object[]{});
        when(methodSignature.getName()).thenReturn("findById");
        when(methodSignature.getParameterNames()).thenReturn(new String[]{});
        when(methodSignature.getParameterTypes()).thenReturn(new Class[]{});
        when(point.proceed()).thenReturn(JsonApiObject.wrap(Duty.newBuilder().setName("abc").setValue(5).build()).build());

        logbackVerifier.expectMessage(INFO, "[JobOffer] Find by id.");
        logbackVerifier.expectMessage(INFO, "[JobOffer] Returning [Duty(");
        logbackVerifier.expectMessage(INFO, "[JobOffer] Find by id took");

        loggingAspect.around(point, apiController);
    }

    @Test
    public void singleCustomParam() throws Throwable {
        setUp();
        when(point.getArgs()).thenReturn(new Object[]{Duty.newBuilder().setName("abc").setValue(5).build()});
        when(methodSignature.getName()).thenReturn("create");
        when(methodSignature.getParameterNames()).thenReturn(new String[]{"duty"});
        when(methodSignature.getParameterTypes()).thenReturn(new Class[]{Duty.class});

        logbackVerifier.expectMessage(INFO, "[JobOffer] Create.");
        logbackVerifier.expectMessage(INFO, "[JobOffer] Details: [duty: Duty(name=abc, value=5, cachedHashCode=2987940)].");
        logbackVerifier.expectMessage(INFO, "[JobOffer] Create took");

        loggingAspect.around(point, apiController);
    }

    @Test
    public void multipleCustomParam() throws Throwable {
        setUp();
        when(point.getArgs()).thenReturn(new Object[]{Duty.newBuilder().setName("abc").setValue(5).build(),
                Duty.newBuilder().setName("xxx").setValue(10).build()});
        when(methodSignature.getName()).thenReturn("doIt");
        when(methodSignature.getParameterNames()).thenReturn(new String[]{"duty", "duty2"});
        when(methodSignature.getParameterTypes()).thenReturn(new Class[]{Duty.class, Duty.class});

        logbackVerifier.expectMessage(INFO, "[JobOffer] Do it.");
        logbackVerifier.expectMessage(INFO, "Details: [duty: Duty(name=abc, value=5, cachedHashCode=2987940), duty2: Duty(name=xxx, value=10, cachedHashCode=3694931)]");
        logbackVerifier.expectMessage(INFO, "[JobOffer] Do it took");

        loggingAspect.around(point, apiController);
    }

    private void setUp() {
        when(apiController.resourceName()).thenReturn("JobOffer");
        CustomFeignProperties loggerClass = new CustomFeignProperties();
        when(point.getTarget()).thenReturn(loggerClass);
        when(point.getSignature()).thenReturn(methodSignature);
    }
}