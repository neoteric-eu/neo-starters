package com.neoteric.starter.test.saasmgr;

import com.neoteric.starter.saasmgr.model.AccountStatus;

import java.lang.annotation.*;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface FixedSaasMgr {

    String customerId() default "customerId";
    String customerName() default "customerName";
    String email() default "email";
    String userId() default "userId";
    AccountStatus accountStatus() default AccountStatus.ACTIVE;
    String[] features() default {};
}
