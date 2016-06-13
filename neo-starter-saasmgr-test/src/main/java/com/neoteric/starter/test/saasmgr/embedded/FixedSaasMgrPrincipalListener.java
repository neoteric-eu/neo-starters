package com.neoteric.starter.test.saasmgr.embedded;

import com.neoteric.starter.saasmgr.auth.SaasMgrAuthenticationProvider;
import com.neoteric.starter.saasmgr.auth.SaasMgrAuthenticationToken;
import com.neoteric.starter.saasmgr.filter.SaasMgrAuthenticationMatcher;
import com.neoteric.starter.saasmgr.model.SubscriptionConstraint;
import com.neoteric.starter.saasmgr.principal.DefaultSaasMgrPrincipal;
import com.neoteric.starter.saasmgr.principal.SaasMgrPrincipal;
import com.neoteric.starter.test.utils.TestContextHelper;
import org.assertj.core.util.Lists;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

public class FixedSaasMgrPrincipalListener extends AbstractTestExecutionListener {

    @Override
    public void beforeTestClass(TestContext testContext) throws Exception {
        TestContextHelper contextHelper = new TestContextHelper(testContext);
        FixedSaasMgr classFixedSaasMgr = contextHelper.getTestClassAnnotation(FixedSaasMgr.class);
        if (classFixedSaasMgr == null) {
            return;
        }

        SaasMgrAuthenticationMatcher saasMatcher = contextHelper.getBean(SaasMgrAuthenticationMatcher.class);
        when(saasMatcher.matches(any())).thenReturn(true);
        mockProviderManager(contextHelper, classFixedSaasMgr);
    }

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        TestContextHelper contextHelper = new TestContextHelper(testContext);
        FixedSaasMgr methodFixedSaasMgr = contextHelper.getTestMethodAnnotation(FixedSaasMgr.class);
        if (methodFixedSaasMgr == null) {
            return;
        }
        verifyClassAnnotation(contextHelper);
        mockProviderManager(contextHelper, methodFixedSaasMgr);
    }

    @Override
    public void afterTestMethod(TestContext testContext) throws Exception {
        TestContextHelper contextHelper = new TestContextHelper(testContext);
        FixedSaasMgr methodFixedSaasMgr = contextHelper.getTestMethodAnnotation(FixedSaasMgr.class);
        if (methodFixedSaasMgr == null) {
            return;
        }
        verifyClassAnnotation(contextHelper);
        FixedSaasMgr classFixedClock = contextHelper.getTestClassAnnotation(FixedSaasMgr.class);
        mockProviderManager(contextHelper, classFixedClock);
    }

    @Override
    public void afterTestClass(TestContext testContext) throws Exception {
        TestContextHelper contextHelper = new TestContextHelper(testContext);
        FixedSaasMgr annotation = contextHelper.getTestClassAnnotation(FixedSaasMgr.class);
        if (annotation == null) {
            return;
        }
        reset(contextHelper.getBean(SaasMgrAuthenticationMatcher.class));
        reset(contextHelper.getBean(SaasMgrAuthenticationProvider.class));
    }

    private void verifyClassAnnotation(TestContextHelper helper) {
        FixedSaasMgr classAnnotation = helper.getTestClassAnnotation(FixedSaasMgr.class);
        if (classAnnotation == null) {
            throw new IllegalStateException("@FixedSaasMgr class level annotation is missing.");
        }
    }

    private void mockProviderManager(TestContextHelper helper, FixedSaasMgr fixedSaasMgr) {
        SaasMgrPrincipal principal = DefaultSaasMgrPrincipal.builder()
                .customerId(fixedSaasMgr.customerId())
                .customerName(fixedSaasMgr.customerName())
                .userId(fixedSaasMgr.userId())
                .email(fixedSaasMgr.email())
                .status(fixedSaasMgr.accountStatus())
                .features(Lists.newArrayList(fixedSaasMgr.features()))
                .constraints(getConstraints(fixedSaasMgr.constraints()))
                .build();

        Authentication token = new SaasMgrAuthenticationToken(principal, "fixed Credentials", principal.getAuthorities());
        SaasMgrAuthenticationProvider authenticationProvider = helper.getBean(SaasMgrAuthenticationProvider.class);
        when(authenticationProvider.authenticate(any())).thenReturn(token);
        when(authenticationProvider.supports(any())).thenReturn(true);
    }

    private List<SubscriptionConstraint> getConstraints(String[] constraints) {
        return Arrays.stream(constraints)
                .map(con -> {
                    String[] splitted = con.split(";");
                    return new SubscriptionConstraint(splitted[0], Double.valueOf(splitted[1]), Double.valueOf(splitted[2]));
                })
                .collect(Collectors.toList());
    }
}