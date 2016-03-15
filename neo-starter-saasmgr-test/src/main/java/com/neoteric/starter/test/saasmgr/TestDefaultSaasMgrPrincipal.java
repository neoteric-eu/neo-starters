package com.neoteric.starter.test.saasmgr;

import com.neoteric.starter.saasmgr.auth.DefaultSaasMgrPrincipal;
import com.neoteric.starter.saasmgr.client.model.AccountStatus;

import java.util.List;

public class TestDefaultSaasMgrPrincipal extends DefaultSaasMgrPrincipal {

    public TestDefaultSaasMgrPrincipal(String userId, String email, String customerId, String customerName, List<String> features, AccountStatus status) {
        super(userId, email, customerId, customerName, features, status);
    }
}
