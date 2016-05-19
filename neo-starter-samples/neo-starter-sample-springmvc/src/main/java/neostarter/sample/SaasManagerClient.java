package neostarter.sample;

import neostarter.sample.model.Customer;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("saasManager")
public interface SaasManagerClient {
    @RequestMapping(method = RequestMethod.GET, value = "/api/v1/users")
    String getSaasError();
}