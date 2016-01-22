package neostarter.sample;

import com.neoteric.starter.jersey.AbstractJerseyConfig;
import org.springframework.stereotype.Component;

@Component
public class JerseyConfig extends AbstractJerseyConfig {

    @Override
    protected void configure() {
        register(Endpoint.class);
    }
}
