package com.neoteric.starter.auth;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.neoteric.starter.auth.basics.AccountStatus;
import com.neoteric.starter.auth.basics.CustomerBasicInfo;
import com.neoteric.starter.auth.basics.LoginInfo;
import com.neoteric.starter.auth.basics.UserAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

@Service
@EnableFeignClients
public class UserAuthenticationFetcher {

    @Autowired
    SaasClient saasClient;

    public UserAuthentication getUserAuthentication(String token, String customerId) {

        checkArgument(!Strings.isNullOrEmpty(token), NeoHeaders.AUTHORIZATION.getValue() + " header must not be null");
        checkArgument(!Strings.isNullOrEmpty(customerId), NeoHeaders.X_CUSTOMER_ID.getValue() + " header must not be null");
        LoginInfo loginInfo = saasClient.getLoginInfo(token, customerId);

        return extractUserAuthenticationFromLogin(loginInfo, customerId);
    }

    private UserAuthentication extractUserAuthenticationFromLogin(LoginInfo loginInfo, String customerId) {
        String userId = loginInfo.getUser().getId();
        String username = loginInfo.getUser().getEmail();

        List<String> features = Lists.newArrayList();
        AccountStatus userStatus = null;
        for (CustomerBasicInfo customerBasicInfo : loginInfo.getUser().getCustomers()) {
            if (customerId.equals(customerBasicInfo.getCustomerId())) {
                features = geFeaturesWithPrefix(customerBasicInfo.getFeatureKeys());
                userStatus = customerBasicInfo.getStatus();
            }
        }
        checkArgument((AccountStatus.ACTIVE.equals(userStatus)), "User status for customer: " + customerId + "is" + userStatus);

        return new UserAuthentication(userId, username, customerId, features);
    }

    private List<String> geFeaturesWithPrefix(List<String> originalFeatures) {
        String rolePrefix = "ROLE_";
        List<String> features = Lists.newArrayList();
        for (String feature : originalFeatures) {
            if (!feature.startsWith(rolePrefix)) {
                features.add(rolePrefix + feature);
            }
        }
        return features;
    }
}
