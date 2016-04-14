package com.neoteric.starter.mvc;

import org.springframework.web.context.support.StaticWebApplicationContext;

public class ClassNameAwareRequestMappingHandlerMappingTest {

    private final StaticWebApplicationContext wac = new StaticWebApplicationContext();
    private final ClassNameAwareRequestMappingHandlerMapping handlerMapping = new ClassNameAwareRequestMappingHandlerMapping();
    {
        this.handlerMapping.setApplicationContext(wac);
    }

}