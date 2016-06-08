package neostarter.sample;

import com.jayway.restassured.RestAssured;
import neostarter.sample.model.Customer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Clock;
import java.time.ZonedDateTime;

import static com.jayway.restassured.RestAssured.given;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ContainerIntegrationTest {

    @MockBean
    GreeterClient greeterClient;

    @MockBean
    Clock clock;

    @LocalServerPort
    int port;

    @Before
    public void setUp() {
        RestAssured.port = this.port;
    }

    @Test
    public void test() throws Exception {

        when(greeterClient.getCustomerToGreet())
                .thenReturn(Customer.builder()
                        .firstName("John")
                        .lastName("Doe")
                        .time(ZonedDateTime.now())
                        .build());

        given()
                .when()
                .get("/api/second")
                .then()
                .statusCode(200);

    }
}