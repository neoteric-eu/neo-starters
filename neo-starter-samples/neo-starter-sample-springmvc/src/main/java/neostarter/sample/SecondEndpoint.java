package neostarter.sample;

import com.neoteric.starter.mvc.ApiController;
import com.neoteric.starter.mvc.annotation.GetJson;

@ApiController
public class SecondEndpoint {

    private final GreeterClient greeterClient;

    SecondEndpoint(GreeterClient greeterClient) {
        this.greeterClient = greeterClient;
    }

    @GetJson
    public String greeter() {
        return "Hello " + greeterClient.getCustomerToGreet().getFirstName();
    }
}
