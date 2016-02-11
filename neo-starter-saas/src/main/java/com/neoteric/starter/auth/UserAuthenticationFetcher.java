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
import java.util.Optional;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

@Service
@EnableFeignClients
public class UserAuthenticationFetcher {

    @Autowired
    private SaasClient saasClient;
    private static final String ROLE_PREFIX = "ROLE_";

    public UserAuthentication getUserAuthentication(String token, String customerId) {

        checkArgument(!Strings.isNullOrEmpty(token), NeoHeaders.AUTHORIZATION.getValue() + " header must not be null");
        checkArgument(!Strings.isNullOrEmpty(customerId), NeoHeaders.X_CUSTOMER_ID.getValue() + " header must not be null");
        LoginInfo loginInfo = saasClient.getLoginInfo(token, customerId);

        return extractUserAuthenticationFromLogin(loginInfo, customerId);
    }

    private UserAuthentication extractUserAuthenticationFromLogin(LoginInfo loginInfo, String customerId) {
        String userId = loginInfo.getUser().getId();
        String email = loginInfo.getUser().getEmail();

        List<String> features = Lists.newArrayList();
        AccountStatus userStatus = null;
        Optional<CustomerBasicInfo> customerBasicInfo = loginInfo.getUser().getCustomers().stream()
                .filter(customerInfo -> customerId.equals(customerInfo.getCustomerId()))
                .findFirst();

        if (customerBasicInfo.isPresent()) {
            features = geFeaturesWithPrefix(customerBasicInfo.get().getFeatureKeys());
            userStatus = customerBasicInfo.get().getStatus();
        }

        checkArgument((AccountStatus.ACTIVE.equals(userStatus)), "User status for customer: " + customerId + "is" + userStatus);

        return new UserAuthentication(userId, email, customerId, features);
    }

    private List<String> geFeaturesWithPrefix(List<String> originalFeatures) {

        return originalFeatures.stream()
                .filter(feature -> !feature.startsWith(ROLE_PREFIX))
                .map(feature -> ROLE_PREFIX + feature)
                .collect(Collectors.toList());
    }
}
