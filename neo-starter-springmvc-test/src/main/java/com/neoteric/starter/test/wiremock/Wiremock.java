package com.neoteric.starter.test.wiremock;

import com.netflix.loadbalancer.ILoadBalancer;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockReset;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@MockBean(value = ILoadBalancer.class, reset = MockReset.NONE)
public @interface Wiremock {
}
