package neostarter.sample;

import com.neoteric.starter.test.SpringBootMockMvcTest;
import com.neoteric.starter.test.clock.FixedClock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static com.jayway.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@RunWith(SpringRunner.class)
@SpringBootMockMvcTest
@FixedClock
public class StarterMockMvcIntegrationTest {

    @Test
    public void test() throws Exception {

        given()
                .when()
                .get("/api/first")
                .then()
                .statusCode(200);
    }
}