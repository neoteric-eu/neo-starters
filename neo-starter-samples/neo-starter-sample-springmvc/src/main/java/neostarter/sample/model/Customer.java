package neostarter.sample.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.time.ZonedDateTime;

@Value
@Builder(toBuilder = true)
@AllArgsConstructor
public class Customer {

    @JsonProperty("first")
    String firstName;

    @JsonProperty("last")
    String lastName;

    ZonedDateTime time;
}
