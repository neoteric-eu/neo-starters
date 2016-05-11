package neostarter.sample;

import neostarter.sample.model.Customer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignClientsRegistrar;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZonedDateTime;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = SecondEndpoint.class)
public class SecondEndpointMvcTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    GreeterClient greeterClient;

    @Test
    public void testSecondEndpoint() throws Exception {

        given(greeterClient.getCustomerToGreet())
                .willReturn(Customer.builder()
                        .firstName("Joeaaa")
                        .lastName("Doe")
                        .time(ZonedDateTime.now())
                        .build());


        mvc.perform(get("/second")).andExpect(status().isOk());
    }
}