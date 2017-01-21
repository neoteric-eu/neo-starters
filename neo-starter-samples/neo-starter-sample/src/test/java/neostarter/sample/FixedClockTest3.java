package neostarter.sample;

import com.jayway.restassured.RestAssured;
import eu.neoteric.starter.test.clock.FixedClock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static com.jayway.restassured.RestAssured.given;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@FixedClock
public class FixedClockTest3 {

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
    @FixedClock("2011-11-11T11:00:00Z")
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
