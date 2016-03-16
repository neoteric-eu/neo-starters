package com.neoteric.starter.test.restassured;

import org.springframework.boot.test.WebIntegrationTest;

import java.lang.annotation.*;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@WebIntegrationTest(randomPort = true)
public @interface ContainerIntegrationTest {
}
