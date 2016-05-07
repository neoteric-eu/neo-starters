package neostarter.sample;

import com.neoteric.starter.mvc.ApiController;
import com.neoteric.starter.mvc.annotation.GetJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

@ApiController
public class SecondEndpoint {

    @Autowired
    RestTemplate restTemplate;

    private final GreeterClient greeterClient;

    SecondEndpoint(GreeterClient greeterClient) {
        this.greeterClient = greeterClient;
    }

    @GetJson
    public String greeter() {
        System.out.println(restTemplate);
        return "Hello " + greeterClient.getCustomerToGreet().getFirstName();
    }
}
