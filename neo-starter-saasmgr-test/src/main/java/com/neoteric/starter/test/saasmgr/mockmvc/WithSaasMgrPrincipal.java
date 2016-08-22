package com.neoteric.starter.test.saasmgr.mockmvc;

import com.neoteric.starter.saasmgr.model.AccountStatus;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.*;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@WithSecurityContext(factory = WithSaasMgrPrincipalContextFactory.class)
public @interface WithSaasMgrPrincipal {

    boolean noAuth() default false;
    String customerId() default "customerId";
    String customerName() default "customerName";
    String email() default "email";
    String firstName() default "firstName";
    String lastName() default "lastName";
    String userId() default "userId";
    AccountStatus accountStatus() default AccountStatus.ACTIVE;
    String[] features() default {};

    /**
     * Pattern: "KEY;0.5;10.0" Key name; max value; current value
     */
    String[] constraints() default {};
}