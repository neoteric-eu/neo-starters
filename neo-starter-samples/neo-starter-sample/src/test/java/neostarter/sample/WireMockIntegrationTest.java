package neostarter.sample;

import com.neoteric.starter.test.SpringBootEmbeddedTest;
import com.neoteric.starter.test.wiremock.WireMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.jayway.restassured.RestAssured.given;

@RunWith(SpringRunner.class)
@SpringBootEmbeddedTest
@WireMock
public class WireMockIntegrationTest {

    @Test
    public void test() throws Exception {

        stubFor(get(urlEqualTo("/api/first"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"first\":\"John\",\"last\":\"Doe\"}")));

        given()
                .when()
                .get("/api/second")
                .then()
                .log().all()
                .statusCode(200);

    }
}
