package com.neoteric.starter.test.jersey.saasmgr.mockmvc;

import com.neoteric.starter.saasmgr.model.AccountStatus;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.*;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@WithSecurityContext(factory = WithSaasMgrPrincipalContextFactory.class)
public @interface WithSaasMgrPrincipal {

    String customerId() default "customerId";
    String customerName() default "customerName";
    String email() default "email";
    String userId() default "userId";
    AccountStatus accountStatus() default AccountStatus.ACTIVE;
    String[] features() default {};

    /**
     * Pattern: "KEY;0.5;10.0" Key name; max value; current value
     */
    String[] constraints() default {};
}