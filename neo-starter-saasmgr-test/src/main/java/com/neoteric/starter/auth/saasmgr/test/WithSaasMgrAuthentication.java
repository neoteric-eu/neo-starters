package com.neoteric.starter.auth.saasmgr.test;

import java.lang.annotation.*;

@Target({ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface WithSaasMgrAuthentication {

    String customerId() default "customerId";
    String customerName() default "customerName";
    String email() default "email@email.com";
    String[] features() default {};
}
