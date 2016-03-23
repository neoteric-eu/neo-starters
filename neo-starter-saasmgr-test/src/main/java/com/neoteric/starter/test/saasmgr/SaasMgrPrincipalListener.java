package com.neoteric.starter.test.saasmgr;

import com.neoteric.starter.saasmgr.client.model.AccountStatus;
import com.neoteric.starter.test.saasmgr.auth.StaticSaasMgrPrincipal;
import lombok.Builder;
import lombok.Value;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import static com.neoteric.starter.test.saasmgr.StarterSaasTestProfiles.FIXED_SAAS_MGR;

public class SaasMgrPrincipalListener extends AbstractTestExecutionListener {

    @Value
    @Builder
    private static class Details {
        String customerId;
        String customerName;
        String userId;
        String email;
        AccountStatus accountStatus;
        String[] features;
    }

    private static final ThreadLocal<Details> DETAILS_HOLDER = new ThreadLocal<>();

    @Override
    public void beforeTestClass(TestContext testContext) throws Exception {
        if (StarterTestUtils.doesNotHaveActiveProfile(testContext, FIXED_SAAS_MGR)) {
            return;
        }
        FixedSaasMgr annotation = testContext.getTestClass().getAnnotation(FixedSaasMgr.class);
        if (annotation == null) {
            throw new IllegalStateException("Test class with 'fixedSaasMgr' profile should be annotated with @FixedSaasMgr");
        }

        DETAILS_HOLDER.set(Details.builder()
                .customerId(annotation.customerId())
                .userId(annotation.userId())
                .email(annotation.email())
                .features(annotation.features())
                .accountStatus(annotation.accountStatus())
                .customerName(annotation.customerName())
                .build());

        setPrincipal(annotation);
    }

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        if (StarterTestUtils.doesNotHaveActiveProfile(testContext, FIXED_SAAS_MGR)) {
            return;
        }
        FixedSaasMgr annotation = testContext.getTestMethod().getAnnotation(FixedSaasMgr.class);
        if (annotation == null) {
            return;
        }
        setPrincipal(annotation);
    }

    @Override
    public void afterTestMethod(TestContext testContext) throws Exception {
        if (StarterTestUtils.doesNotHaveActiveProfile(testContext, FIXED_SAAS_MGR)) {
            return;
        }
        FixedSaasMgr annotation = testContext.getTestMethod().getAnnotation(FixedSaasMgr.class);
        if (annotation == null) {
            return;
        }
        setPrincipal(DETAILS_HOLDER.get());
    }

    @Override
    public void afterTestClass(TestContext testContext) throws Exception {
        if (StarterTestUtils.doesNotHaveActiveProfile(testContext, FIXED_SAAS_MGR)) {
            return;
        }
        DETAILS_HOLDER.remove();
    }

    private void setPrincipal(FixedSaasMgr annotation) {
        StaticSaasMgrPrincipal.customerId = annotation.customerId();
        StaticSaasMgrPrincipal.customerName = annotation.customerName();
        StaticSaasMgrPrincipal.userId = annotation.userId();
        StaticSaasMgrPrincipal.email = annotation.email();
        StaticSaasMgrPrincipal.accountStatus = annotation.accountStatus();
        StaticSaasMgrPrincipal.features = annotation.features();
    }

    private void setPrincipal(Details details) {
        StaticSaasMgrPrincipal.customerId = details.getCustomerId();
        StaticSaasMgrPrincipal.customerName = details.getCustomerName();
        StaticSaasMgrPrincipal.userId = details.getUserId();
        StaticSaasMgrPrincipal.email = details.getEmail();
        StaticSaasMgrPrincipal.accountStatus = details.getAccountStatus();
        StaticSaasMgrPrincipal.features = details.getFeatures();
    }
}
