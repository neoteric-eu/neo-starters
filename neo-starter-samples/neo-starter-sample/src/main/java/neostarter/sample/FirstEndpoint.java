package neostarter.sample;

import eu.neoteric.starter.mvc.ApiController;
import eu.neoteric.starter.mvc.annotation.GetJson;
import neostarter.sample.model.Customer;

@ApiController
public class FirstEndpoint {

    private final CustomerFetcher customerFetcher;

    public FirstEndpoint(CustomerFetcher customerFetcher) {
        this.customerFetcher = customerFetcher;
    }

    @GetJson
    public Customer getCustomer() {
        return customerFetcher.get();
    }

}
