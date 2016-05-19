package neostarter.sample;

import neostarter.sample.model.Customer;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.ZonedDateTime;

@Component
public class CustomerFetcher {

    private final Clock clock;

    public CustomerFetcher(Clock clock) {
        this.clock = clock;
    }

    public Customer get() {
        Customer customer = Customer.builder()
                .firstName("John")
                .lastName("Doe")
                .time(ZonedDateTime.now(clock)).build();
        return customer;
    }
}
