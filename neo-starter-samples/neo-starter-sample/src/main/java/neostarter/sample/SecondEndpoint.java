package neostarter.sample;

import eu.neoteric.starter.mvc.ApiController;
import eu.neoteric.starter.mvc.annotation.GetJson;
import lombok.AllArgsConstructor;

@ApiController
@AllArgsConstructor
public class SecondEndpoint {

    private final GreeterClient greeterClient;

    @GetJson
    public String greeter() {
        return "Hello " + greeterClient.getCustomerToGreet().getFirstName();
    }
}
