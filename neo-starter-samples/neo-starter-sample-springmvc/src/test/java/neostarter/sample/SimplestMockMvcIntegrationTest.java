package neostarter.sample;

import neostarter.sample.model.Customer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZonedDateTime;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SimplestMockMvcIntegrationTest {

    @MockBean
    GreeterClient greeterClient;

    @Autowired
    MockMvc mvc;

    @Test
    public void test() throws Exception {

        given(greeterClient.getCustomerToGreet())
                .willReturn(Customer.builder()
                        .firstName("John")
                        .lastName("Doe")
                        .time(ZonedDateTime.now())
                        .build());


        mvc.perform(get("/api/second")).andExpect(status().isOk());

    }
}