package com.neoteric.starter.test.saasmgr;

import com.neoteric.starter.saasmgr.client.model.AccountStatus;

import java.lang.annotation.*;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface FixedSaasMgr {

    String DEFAULT_CUSTOMER_ID = "customerId";
    String DEFAULT_CUSTOMER_NAME = "customerName";
    String DEFAULT_EMAIL = "email";
    String DEFAULT_USER_ID = "userId";
    AccountStatus DEFAULT_STATUS = AccountStatus.ACTIVE;

    String customerId() default "customerId";
    String customerName() default "customerName";
    String email() default "email";
    String userId() default "userId";
    AccountStatus accountStatus() default AccountStatus.ACTIVE;
    String[] features() default {};
}
