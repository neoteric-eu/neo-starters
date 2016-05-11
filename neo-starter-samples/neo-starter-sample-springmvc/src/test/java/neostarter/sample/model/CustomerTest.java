package neostarter.sample.model;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@JsonTest
public class CustomerTest {


    private JacksonTester<Customer> json;

    @Test
    public void serializeJson() throws Exception {
        Customer details = new Customer("John", "Doe", ZonedDateTime.now());

        assertThat(this.json.write(details))
                .extractingJsonPathStringValue("@.make")
                .isEqualTo("Honda");
    }
}