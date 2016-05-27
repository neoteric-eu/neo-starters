package neostarter.sample;

import com.neoteric.starter.test.SpringBootEmbeddedTest;
import com.neoteric.starter.test.jersey.saasmgr.embedded.FixedSaasMgr;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import static com.jayway.restassured.RestAssured.get;

@RunWith(SpringRunner.class)
@SpringBootEmbeddedTest
@FixedSaasMgr(customerName = "aaa", features = "NF_USER")
public class SecuredEndpointClientTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();


    @Test
    public void shouldEnterWithSaasPrincipalViaRestAssuredMockMVC() throws Exception {
        get("/api/secured/saas")
                .then()
                .log().all()
                .statusCode(200);
    }

    @Test
    @FixedSaasMgr(customerName = "XXX", features = "NF_USER")
    public void shouldEnterWithSaasPrincipalViaRestAssuredMockMVCOnMethod() throws Exception {
        get("/api/secured/saas")
                .then()
                .log().all()
                .statusCode(200);
    }

    @Test
    public void shouldEnterWithSaasPrincipalViaRestAssuredMockMVC3() throws Exception {
        get("/api/secured/saas")
                .then()
                .log().all()
                .statusCode(200);
    }
}