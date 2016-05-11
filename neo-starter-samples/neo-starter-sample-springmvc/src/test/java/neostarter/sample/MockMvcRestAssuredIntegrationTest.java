package neostarter.sample;

import com.jayway.restassured.module.mockmvc.RestAssuredMockMvc;
import neostarter.sample.model.Customer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.time.ZonedDateTime;

import static com.jayway.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MockMvcRestAssuredIntegrationTest {

    @MockBean
    GreeterClient greeterClient;

    @Autowired
    MockMvc mvc;

    @Autowired
    WebApplicationContext wac;

    @Before
    public void setUp() {
        RestAssuredMockMvc.webAppContextSetup(wac);
    }

    @After
    public void reset() {
        RestAssuredMockMvc.reset();
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