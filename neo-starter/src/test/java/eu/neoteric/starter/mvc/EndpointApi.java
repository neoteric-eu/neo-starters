package eu.neoteric.starter.mvc;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EndpointApi {

    @GetMapping
    public void get() {
    }

}
