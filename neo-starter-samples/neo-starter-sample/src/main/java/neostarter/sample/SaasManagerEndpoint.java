package neostarter.sample;

import com.neoteric.starter.mvc.ApiController;
import com.neoteric.starter.mvc.annotation.GetJson;

@ApiController
public class SaasManagerEndpoint {

    private final SaasManagerClient managerClient;

    SaasManagerEndpoint(SaasManagerClient managerClient) {
        this.managerClient = managerClient;
    }

    @GetJson
    public String getFromSaas() {
        return "RETURNED : " + managerClient.getSaasError();
    }
}
