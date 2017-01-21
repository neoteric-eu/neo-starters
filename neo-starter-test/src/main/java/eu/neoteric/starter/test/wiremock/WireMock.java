package eu.neoteric.starter.test.wiremock;

import com.netflix.loadbalancer.AbstractLoadBalancer;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockReset;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@MockBean(value = AbstractLoadBalancer.class, reset = MockReset.NONE)
public @interface WireMock {
}
