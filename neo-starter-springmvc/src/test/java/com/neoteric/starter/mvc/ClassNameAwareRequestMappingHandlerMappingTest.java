package com.neoteric.starter.mvc;

import com.google.common.base.CaseFormat;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import static org.assertj.core.api.Assertions.*;

@RunWith(JUnitParamsRunner.class)
public class ClassNameAwareRequestMappingHandlerMappingTest {

    private final StaticWebApplicationContext wac = new StaticWebApplicationContext();
    private ClassNameAwareRequestMappingHandlerMapping handlerMapping;
    private StarterMvcProperties.ApiProperties apiProps;

    private void mappingHandler(StarterMvcProperties.ApiProperties apiProperties) {
        this.handlerMapping = new ClassNameAwareRequestMappingHandlerMapping(apiProperties);
        this.handlerMapping.setApplicationContext(wac);
    }

    private void assertPattern(RequestMappingInfo info, String pattern) {
        assertThat(info).isNotNull();
        assertThat(info.getPatternsCondition().getPatterns())
                .hasSize(1)
                .contains(pattern);
    }

    @Before
    public void setUpApiProps() {
        apiProps = new StarterMvcProperties.ApiProperties();
    }

    @Test
    public void shouldNotAddAnyPrefixIfNoConfigProvidedForApiController() throws Exception {
        mappingHandler(apiProps);
        RequestMappingInfo info = handlerMapping.getMappingForMethod(ListEndpointApiController.class.getMethod("getList"), ListEndpointApiController.class);
        assertPattern(info, "/list-endpoint-api-controller/getList");
    }

    @Test
    @Parameters({"api", "/api", "api/", "/api/"})
    public void addApiPathPrefixWhenSetUpForApiController(String apiPath) throws Exception {
        apiProps.setPath(apiPath);
        mappingHandler(apiProps);
        RequestMappingInfo info = handlerMapping.getMappingForMethod(ListEndpointApiController.class.getMethod("getList"), ListEndpointApiController.class);
        assertPattern(info, "/api/list-endpoint-api-controller/getList");
    }

    @Test
    @Parameters({"v1", "/v1", "v1/", "/v1/"})
    public void addPrefixWhenDefaultPrefixSetUpForApiController(String defaultPrefix) throws Exception {
        apiProps.getResources().setDefaultPrefix(defaultPrefix);
        mappingHandler(apiProps);
        RequestMappingInfo info = handlerMapping.getMappingForMethod(ListEndpointApiController.class.getMethod("getList"), ListEndpointApiController.class);
        assertPattern(info, "/v1/list-endpoint-api-controller/getList");
    }

    @Test
    @Parameters({
            "List?Controller, /endpoint-api/getList",
            "ListController, /list-endpoint-api-controller/getList",
            "List?Controller?, /endpoint-api/getList",
            "?Controller, /list-endpoint-api/getList",
            "List?, /endpoint-api-controller/getList",
            "SomeList?, /list-endpoint-api-controller/getList", // won't match
            "List?ControllerEnd, /list-endpoint-api-controller/getList", // won't match
            "?, /list-endpoint-api-controller/getList"})
    public void shouldHandlePrefixAndSuffixWhenPatternProvided(String pattern, String expectedUrl) throws Exception {
        apiProps.getResources().setClassNamePattern(pattern);
        mappingHandler(apiProps);
        RequestMappingInfo info = handlerMapping.getMappingForMethod(ListEndpointApiController.class.getMethod("getList"), ListEndpointApiController.class);
        assertPattern(info, expectedUrl);
    }

    @Test
    public void shouldIntegrateEveryOptionForApiController() throws Exception {
        apiProps.setPath("/api");
        apiProps.getResources().setDefaultPrefix("v1");
        apiProps.getResources().setClassNamePattern("List?Controller");
        mappingHandler(apiProps);
        RequestMappingInfo info = handlerMapping.getMappingForMethod(ListEndpointApiController.class.getMethod("getList"), ListEndpointApiController.class);
        assertPattern(info, "/api/v1/endpoint-api/getList");
    }

    @Test
    public void shouldNotProcessApiOptionsForRestController() throws Exception {
        apiProps.setPath("/api");
        apiProps.getResources().setDefaultPrefix("v1");
        apiProps.getResources().setClassNamePattern("List?Controller");
        mappingHandler(apiProps);
        RequestMappingInfo info = handlerMapping.getMappingForMethod(ListRestController.class.getMethod("getList"), ListRestController.class);
        assertPattern(info, "/getList");
    }

    @Test
    public void shouldFormatName() throws Exception {
        apiProps.getResources().setClassNamePattern("List?Controller");
        apiProps.getResources().setCaseFormat(CaseFormat.LOWER_CAMEL);
        mappingHandler(apiProps);
        RequestMappingInfo info = handlerMapping.getMappingForMethod(ListEndpointApiController.class.getMethod("getList"), ListEndpointApiController.class);
        assertPattern(info, "/endpointApi/getList");
    }

    @Test
    public void shouldUseExplicitPrefixForApiControler() throws Exception {
        apiProps.getResources().setDefaultPrefix("v1");
        apiProps.getResources().setClassNamePattern("?Controller");
        mappingHandler(apiProps);
        RequestMappingInfo info = handlerMapping.getMappingForMethod(PrefixedApiController.class.getMethod("get"), PrefixedApiController.class);
        assertPattern(info, "/v2/prefixed-api/get");
    }

    @Test
    public void shouldAddRequestMappingValueAfterApiControllerPrefixes() throws Exception {
        apiProps.getResources().setDefaultPrefix("v1");
        apiProps.getResources().setClassNamePattern("?Controller");
        mappingHandler(apiProps);
        RequestMappingInfo info = handlerMapping.getMappingForMethod(ClassMappingController.class.getMethod("get"), ClassMappingController.class);
        assertPattern(info, "/v1/class-mapping/hi/get");
    }

    @RestController
    static class ListRestController {

        @GetMapping("/getList")
        public void getList() {
        }

    }

    @ApiController
    static class ListEndpointApiController {

        @GetMapping("/getList")
        public void getList() {
        }

    }

    @ApiController(prefix = "v2")
    static class PrefixedApiController {

        @GetMapping("/get")
        public void get() {
        }

    }

    @ApiController
    @RequestMapping("/hi")
    static class ClassMappingController {

        @GetMapping("/get")
        public void get() {
        }

    }

}