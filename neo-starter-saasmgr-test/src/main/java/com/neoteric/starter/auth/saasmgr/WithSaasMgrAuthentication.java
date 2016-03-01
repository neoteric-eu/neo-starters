package com.neoteric.starter.auth.saasmgr;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.*;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@WithSecurityContext(factory = WithSaasMgrSecurityFactory.class)
public @interface WithSaasMgrAuthentication {

    String customerId() default "customerId";
    String customerName() default "customerName";
    String email() default "email@email.com";
    String[] features() default {};
}
