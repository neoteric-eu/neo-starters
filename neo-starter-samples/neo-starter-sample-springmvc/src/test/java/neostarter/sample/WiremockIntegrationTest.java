package neostarter.sample;

import com.neoteric.starter.test.SpringBootEmbeddedTest;
import com.neoteric.starter.test.clock.FixedClock;
import com.neoteric.starter.test.wiremock.Wiremock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.jayway.restassured.RestAssured.given;

@RunWith(SpringRunner.class)
@SpringBootEmbeddedTest
@Wiremock
public class WiremockIntegrationTest {

    @Test
    public void test() throws Exception {

        stubFor(get(urlEqualTo("/api/first"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "text/plain")
                        .withBody("Hello world!")));

        given()
                .when()
                .get("/api/second")
                .then()
                .log().all()
                .statusCode(200);

    }
}
