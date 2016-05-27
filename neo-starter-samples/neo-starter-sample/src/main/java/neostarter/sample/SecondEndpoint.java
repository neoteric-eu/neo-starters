package neostarter.sample;

import com.neoteric.starter.mvc.ApiController;
import com.neoteric.starter.mvc.annotation.GetJson;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

@ApiController
@AllArgsConstructor
public class SecondEndpoint {

    private final GreeterClient greeterClient;

    @GetJson
    public String greeter() {
        return "Hello " + greeterClient.getCustomerToGreet().getFirstName();
    }
}
