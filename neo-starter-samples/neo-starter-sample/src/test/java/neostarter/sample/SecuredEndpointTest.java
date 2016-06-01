package neostarter.sample;

import com.neoteric.starter.test.SpringBootMockMvcTest;
import com.neoteric.starter.test.saasmgr.mockmvc.WithSaasMgrPrincipal;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;

import static com.jayway.restassured.module.mockmvc.RestAssuredMockMvc.get;
import static org.assertj.core.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootMockMvcTest
public class SecuredEndpointTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Autowired
    SecuredEndpoint endpoint;

    @Test
    public void shouldNotEnter() throws Exception {
        expectedException.expect(AuthenticationCredentialsNotFoundException.class);
        endpoint.greeter();
    }

    @Test
    @WithMockUser(authorities = "NF_USER")
    public void shouldEnter() throws Exception {
        assertThat(endpoint.greeter()).isEqualTo("Hello!");
    }

    @Test
    @WithMockUser(authorities = "NF_USER")
    public void shouldEnterViaRestAssuredMockMVC() throws Exception {
        get("/api/secured")
                .then()
                .statusCode(200);
    }

    @Test
    @WithSaasMgrPrincipal(customerName = "elo", features = "NF_USER")
    public void shouldEnterWithSaasPrincipalViaRestAssuredMockMVC() throws Exception {
        get("/api/secured/saas")
                .then()
                .log().all()
                .statusCode(200);
    }
}