package com.neoteric.starter.saasmgr.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neoteric.starter.saasmgr.client.SaasMgrClient;
import com.neoteric.starter.saasmgr.model.AccountStatus;
import com.neoteric.starter.saasmgr.model.LoginData;
import com.neoteric.starter.saasmgr.model.LoginDataWrapper;
import com.neoteric.starter.saasmgr.principal.SaasMgrPrincipal;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultSaasMgrAuthenticatorTest {

    public static final String CUSTOMER_ID = "5351090b8fe7f4e7b99d6e67";
    @Mock
    private SaasMgrClient mockClient;

    @InjectMocks
    private DefaultSaasMgrAuthenticator authenticator;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldExtractFullPrincipal() throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        LoginDataWrapper loginDataWrapper = mapper.readValue(new File("src/test/resources/__files/authToken.json"), LoginDataWrapper.class);

        when(mockClient.getLoginInfo(anyString(), anyString()))
                .thenReturn(loginDataWrapper.getData());

        SaasMgrPrincipal principal = authenticator.authenticate("xxx", CUSTOMER_ID);
        assertThat(principal.getCustomerId()).isEqualTo(CUSTOMER_ID);
        assertThat(principal.getCustomerName()).isEqualTo("Neoteric clinic-dev");
        assertThat(principal.getUserId()).isEqualTo("5357699c9d33da5ee72b45ce");
        assertThat(principal.getEmail()).isEqualTo("demo@neoteric.eu");
        assertThat(principal.getStatus()).isEqualTo(AccountStatus.ACTIVE);
        assertThat(principal.getAuthorities()).hasSize(9);
        assertThat(principal.getConstraints())
                .hasSize(1)
                .extracting("key", "currentValue", "maxValue")
                .contains(tuple("USER_NUMBER", 0.0, 10.0));
    }

    @Test
    public void shouldThrowUserNotFound() throws Exception {
        when(mockClient.getLoginInfo(anyString(), anyString()))
                .thenReturn(new LoginData(null, null));

        expectedException.expect(UsernameNotFoundException.class);
        authenticator.authenticate("xxx", CUSTOMER_ID);
    }
}