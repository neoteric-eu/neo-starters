package neostarter.sample;

import neostarter.sample.model.Customer;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("sampleApp")
public interface GreeterClient {

    @RequestMapping(method = RequestMethod.GET, value = "/api/first")
    Customer getCustomerToGreet();

}
