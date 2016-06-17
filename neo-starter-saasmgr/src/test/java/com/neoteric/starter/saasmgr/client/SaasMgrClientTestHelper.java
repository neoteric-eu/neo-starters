package com.neoteric.starter.saasmgr.client;

import com.neoteric.starter.saasmgr.model.Customer;
import com.neoteric.starter.saasmgr.model.LoginData;
import com.neoteric.starter.saasmgr.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

public class SaasMgrClientTestHelper {

    protected void assertCompleteData(LoginData loginData) {
        assertThat(loginData.getToken()).isEqualTo("zm6InsRW8rAfywycYsgI4iqHWRTPdnqxmLuV25M82Qh3CJpazLxFTEbEHmA2kpgZZRNXDWkKbxVUyIhJ");
        User user = loginData.getUser();
        assertThat(user.getId()).isEqualTo("5357699c9d33da5ee72b45ce");
        assertThat(user.getEmail()).isEqualTo("demo@neoteric.eu");
        assertThat(user.getCustomers()).hasSize(1);
        Customer customer = user.getCustomers().get(0);

        assertThat(customer.getCustomerId()).isEqualTo("5351090b8fe7f4e7b99d6e67");
        assertThat(customer.getCustomerName()).isEqualTo("Neoteric clinic-dev");
        assertThat(customer.getFeatureKeys()).hasSize(9).contains("NF_ADMIN", "SH_OFFER_UPDATE");
        assertThat(customer.getConstraints())
                .hasSize(1)
                .extracting("key", "currentValue", "maxValue")
                .contains(tuple("USER_NUMBER", 0.0, 10.0));
    }

    protected static void stubForCompleteResponse() {
        stubFor(get(urlEqualTo("/api/v2/users/authInfo"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("authToken.json")));
    }

    protected static void stubForUnauthorized() {
        stubFor(get(urlEqualTo("/api/v2/users/authInfo"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.UNAUTHORIZED.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));
    }


    protected static void stubForServiceUnavailable() {
        stubFor(get(urlEqualTo("/api/v2/users/authInfo"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SERVICE_UNAVAILABLE.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));
    }
}

