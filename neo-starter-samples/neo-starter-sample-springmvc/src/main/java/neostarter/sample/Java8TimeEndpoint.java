package neostarter.sample;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/java8Time")
public class Java8TimeEndpoint {

    @GetMapping("/zonedDateTime/{dateTime}")
    public String getFromSaas(@PathVariable ZonedDateTime dateTime) {
        return "RETURNED : " + DateTimeFormatter.ISO_INSTANT.format(dateTime);
    }
}
