package neostarter.sample;

import com.jayway.restassured.RestAssured;
import neostarter.sample.model.Customer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.web.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Clock;
import java.time.ZonedDateTime;

import static com.jayway.restassured.RestAssured.given;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@NewFixedClock
public class FixedClockTest {

    @LocalServerPort
    int port;

    @Before
    public void setUp() {
        RestAssured.port = this.port;
    }

    @Test
    public void test() throws Exception {

        given()
                .when()
                .get("/api/first")
                .then()
                .log().all()
                .statusCode(200);

    }

    @Test
    @NewFixedClock("2011-11-11T11:00:00Z")
    public void test2() throws Exception {

        given()
                .when()
                .get("/api/first")
                .then()
                .log().all()
                .statusCode(200);

    }

    @Test
    public void test3() throws Exception {

        given()
                .when()
                .get("/api/first")
                .then()
                .log().all()
                .statusCode(200);

    }
}
