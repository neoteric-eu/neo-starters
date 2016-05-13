package com.neoteric.starter.mvc;

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableMap;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import static org.assertj.core.api.Assertions.*;

@RunWith(JUnitParamsRunner.class)
public class ClassNameAwareRequestMappingHandlerMappingTest {

    private final StaticWebApplicationContext wac = new StaticWebApplicationContext();
    private ClassNameAwareRequestMappingHandlerMapping handlerMapping;

    @Before
    public void setUpHandlerMapping() {
        this.handlerMapping = new ClassNameAwareRequestMappingHandlerMapping();
        this.handlerMapping.setApplicationContext(wac);
    }

    @Test
    public void shouldNotAddAnyPrefixIfNoConfigProvided() throws Exception {
        RequestMappingInfo info = handlerMapping.getMappingForMethod(ListController.class.getMethod("getList"), ListController.class);
        assertThat(info).isNotNull();
        assertThat(info.getPatternsCondition().getPatterns())
                .hasSize(1)
                .contains("/getList");
    }

    @Test
    public void shouldAddOnlyClassNamePrefixIfEmptyInitialPrefixProvided() throws Exception {
        handlerMapping.setClassSuffixToPrefix(ImmutableMap.of("Controller", ""));
        RequestMappingInfo info = handlerMapping.getMappingForMethod(ListController.class.getMethod("getList"), ListController.class);
        assertThat(info).isNotNull();
        assertThat(info.getPatternsCondition().getPatterns())
                .hasSize(1)
                .contains("/list/getList");
    }

    @Test
    public void shouldAddFullPrefix() throws Exception {
        handlerMapping.setClassSuffixToPrefix(ImmutableMap.of("Controller", "/api"));
        RequestMappingInfo info = handlerMapping.getMappingForMethod(ListController.class.getMethod("getList"), ListController.class);
        assertThat(info).isNotNull();
        assertThat(info.getPatternsCondition().getPatterns())
                .hasSize(1)
                .contains("/api/list/getList");
    }


    @Test
    @Parameters({"api", "/api", "api/", "/api/"})
    public void shouldHandleAllPrefixPossibilities(String prefix) throws Exception {
        handlerMapping.setClassSuffixToPrefix(ImmutableMap.of("Controller", prefix));
        RequestMappingInfo info = handlerMapping.getMappingForMethod(ListController.class.getMethod("getList"), ListController.class);
        assertThat(info).isNotNull();
        assertThat(info.getPatternsCondition().getPatterns())
                .hasSize(1)
                .contains("/api/list/getList");
    }

    @Test
    public void shouldPickAppropriatePrefix() throws Exception {
        handlerMapping.setClassSuffixToPrefix(ImmutableMap.of("Api2", "/api/v2", "Api", "/api/v1"));
        RequestMappingInfo info = handlerMapping.getMappingForMethod(EndpointApi.class.getMethod("get"), EndpointApi.class);
        assertThat(info).isNotNull();
        assertThat(info.getPatternsCondition().getPatterns())
                .hasSize(1)
                .contains("/api/v1/endpoint");
    }

    @Test
    public void shouldPickFirstEntryIfCollapsingEntries() throws Exception {
        handlerMapping.setClassSuffixToPrefix(ImmutableMap.of("Api", "/api/v1", "pi", "/pi"));
        RequestMappingInfo info = handlerMapping.getMappingForMethod(EndpointApi.class.getMethod("get"), EndpointApi.class);
        assertThat(info).isNotNull();
        assertThat(info.getPatternsCondition().getPatterns())
                .hasSize(1)
                .contains("/api/v1/endpoint");
    }

    @Test
    public void shouldFormatWithHyphenByDefault() throws Exception {
        handlerMapping.setClassSuffixToPrefix(ImmutableMap.of("Controller", ""));
        RequestMappingInfo info = handlerMapping.getMappingForMethod(SomeImportantController.class.getMethod("get"),
                SomeImportantController.class);
        assertThat(info).isNotNull();
        assertThat(info.getPatternsCondition().getPatterns())
                .hasSize(1)
                .contains("/some-important/get");
    }

    @Test
    public void shouldFormatName() throws Exception {
        handlerMapping.setCaseFormat(CaseFormat.LOWER_CAMEL);
        handlerMapping.setClassSuffixToPrefix(ImmutableMap.of("Controller", ""));
        RequestMappingInfo info = handlerMapping.getMappingForMethod(SomeImportantController.class.getMethod("get"),
                SomeImportantController.class);
        assertThat(info).isNotNull();
        assertThat(info.getPatternsCondition().getPatterns())
                .hasSize(1)
                .contains("/someImportant/get");
    }

    @Test
    public void shouldProperlyOrderPrefixAndClassRequestMapping() throws Exception {
        handlerMapping.setCaseFormat(CaseFormat.LOWER_CAMEL);
        handlerMapping.setClassSuffixToPrefix(ImmutableMap.of("Controller", "/api"));
        RequestMappingInfo info = handlerMapping.getMappingForMethod(ImportantController.class.getMethod("get"),
                ImportantController.class);
        assertThat(info).isNotNull();
        assertThat(info.getPatternsCondition().getPatterns())
                .hasSize(1)
                .contains("/api/important/v1/get");
    }


    @Controller
    @RequestMapping("/v1")
    static class ImportantController {

        @GetMapping("/get")
        public void get() {
        }

    }

    @Controller
    static class SomeImportantController {

        @GetMapping("/get")
        public void get() {
        }

    }

    @Controller
    static class ListController {

        @GetMapping("/getList")
        public void getList() {
        }

    }

}