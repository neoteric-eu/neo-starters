package neostarter.sample;

import com.neoteric.starter.mvc.ApiController;
import com.neoteric.starter.mvc.annotation.GetJson;
import com.neoteric.starter.saasmgr.SaasMgrAuthUtils;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;

@ApiController
@AllArgsConstructor
public class SecuredEndpoint {

    @GetJson
    @PreAuthorize("hasAuthority('NF_USER')")
    public String greeter() {
        return "Hello!";
    }

    @GetJson("/saas")
    @PreAuthorize("hasAuthority('NF_USER')")
    public String greeterFromSaas() {
        return "Hello " + SaasMgrAuthUtils.getPrincipal().getCustomerName();
    }
}
