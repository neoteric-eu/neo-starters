package com.neoteric.starter.auth;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.neoteric.starter.auth.basics.AccountStatus;
import com.neoteric.starter.auth.basics.CustomerBasicInfo;
import com.neoteric.starter.auth.basics.LoginInfo;
import com.neoteric.starter.auth.basics.UserSecurityInfo;
import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jaxrs.JAXRSContract;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

@Service
@PropertySource("classpath:default-saas.properties")
public class AuthenticationCreator {

    @Value("${saasManagerHost}")
    String saasManagerHost;

    public Authentication createAuthentication(String token, String customerId) {
        Optional<UserSecurityInfo> userSecurityInfo = getUserSecurityInfo(token, customerId);
        checkUserPresence(token, userSecurityInfo);

        Authentication auth = userSecurityInfo
                .map(userInfo -> new PreAuthenticatedAuthenticationToken(
                        userInfo.getUsername(),
                        token,
                        userInfo.getFeatures().stream().map(feature -> new SimpleGrantedAuthority(feature)).collect(Collectors.toList())))
                .orElseThrow(() -> new BadCredentialsException("invalid token: " + token));

        return auth;
    }

    private void checkUserPresence(String token, Optional<UserSecurityInfo> user) {
        if (!user.isPresent()) {
            throw new BadCredentialsException("invalid token: " + token);
        }
    }

    private Optional getUserSecurityInfo(String token, String customerId) {

        checkArgument(!Strings.isNullOrEmpty(token), NeoHeaders.AUTHORIZATION.getValue() + " header must not be null");
        checkArgument(!Strings.isNullOrEmpty(customerId), NeoHeaders.X_CUSTOMER_ID.getValue() + " header must not be null");
        LoginInfo loginInfo = getLoginInfo(token, customerId);
        UserSecurityInfo userSecurityInfo = extractUserSecurityFromLogin(loginInfo, customerId);

        return Optional.of(userSecurityInfo);
    }

    private LoginInfo getLoginInfo(String token, String customerId) {
        SaasClient saasClient = Feign.builder()
                .contract(new JAXRSContract())
                .decoder(new JacksonDecoder())
                .target(SaasClient.class, saasManagerHost);

        return saasClient.getLoginInfo(token, customerId);
    }

    private UserSecurityInfo extractUserSecurityFromLogin(LoginInfo loginInfo, String customerId) {

        List<String> features = Lists.newArrayList();
        AccountStatus userStatus = null;
        for (CustomerBasicInfo customerBasicInfo : loginInfo.getUser().getCustomers()) {
            if (customerBasicInfo.getCustomerId().equals(customerId)) {
                features = geFeaturesWithPrefix(customerBasicInfo.getFeatureKeys());
                userStatus = customerBasicInfo.getStatus();
            }
        }
        checkArgument((AccountStatus.ACTIVE.equals(userStatus)), "User status for customer: " + customerId + "is" + userStatus);

        return new UserSecurityInfo(loginInfo.getUser().getEmail(), features);
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
