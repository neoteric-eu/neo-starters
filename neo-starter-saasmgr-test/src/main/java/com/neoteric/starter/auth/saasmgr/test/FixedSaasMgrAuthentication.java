package com.neoteric.starter.auth.saasmgr.test;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(FixedSaasMgrRegistrar.class)
public @interface FixedSaasMgrAuthentication {

    String customerId() default "customerId";
    String customerName() default "customerName";
    String email() default "email@email.com";
    String userId() default "userId";
    String[] features() default {};
}