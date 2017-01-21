package neostarter.sample;

import eu.neoteric.starter.test.SpringBootEmbeddedTest;
import eu.neoteric.starter.test.clock.FixedClock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static com.jayway.restassured.RestAssured.given;

@RunWith(SpringRunner.class)
@SpringBootEmbeddedTest(properties = "eao=true")
@TestPropertySource(properties = "xxx=abc")
@FixedClock
public class StarterContainerIntegrationTest {

    @Value("${eao}")
    String param;

    @Value("${xxx}")
    String param2;

    @Test
    public void test() throws Exception {

        System.out.println("XXX " + param);
        System.out.println("XXX " + param2);

        given()
                .when()
                .get("/api/first")
                .then()
                .log().all()
                .statusCode(200);

    }
}