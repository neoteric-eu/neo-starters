package com.neoteric.starter.saasmgr.auth;

import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

//@RunWith(MockitoJUnitRunner.class)
public class DefaultSaasMgrAuthenticatorTest {

//    public static final String CUSTOMER_ID = "5351090b8fe7f4e7b99d6e67";
//    @Mock
//    private SaasMgrClient mockClient;
//
//    @InjectMocks
//    private DefaultSaasMgrAuthenticator authenticator;
//
//    @Rule
//    public ExpectedException expectedException = ExpectedException.none();
//
//    @Test
//    public void shouldExtractFullPrincipal() throws Exception {
//
//        ObjectMapper mapper = new ObjectMapper();
//        LoginDataWrapper loginDataWrapper = mapper.readValue(new File("src/test/resources/__files/authToken.json"), LoginDataWrapper.class);
//
//        when(mockClient.getLoginInfo(anyString(), anyString()))
//                .thenReturn(loginDataWrapper.getData());
//
//        SaasMgrPrincipal principal = authenticator.authenticate("xxx", CUSTOMER_ID);
//        assertThat(principal.getCustomerId()).isEqualTo(CUSTOMER_ID);
//        assertThat(principal.getCustomerName()).isEqualTo("Neoteric clinic-dev");
//        assertThat(principal.getUserId()).isEqualTo("5357699c9d33da5ee72b45ce");
//        assertThat(principal.getEmail()).isEqualTo("demo@neoteric.eu");
//        assertThat(principal.getStatus()).isEqualTo(AccountStatus.ACTIVE);
//        assertThat(principal.getAuthorities()).hasSize(9);
//        assertThat(principal.getConstraints())
//                .hasSize(1)
//                .extracting("key", "currentValue", "maxValue")
//                .contains(tuple("USER_NUMBER", 0.0, 10.0));
//    }
//
//    @Test
//    public void shouldThrowUserNotFound() throws Exception {
//        when(mockClient.getLoginInfo(anyString(), anyString()))
//                .thenReturn(new LoginData(null, null));
//
//        expectedException.expect(UsernameNotFoundException.class);
//        authenticator.authenticate("xxx", CUSTOMER_ID);
//    }
}