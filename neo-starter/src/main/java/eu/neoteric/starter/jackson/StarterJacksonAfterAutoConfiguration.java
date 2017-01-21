package eu.neoteric.starter.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureAfter(JacksonAutoConfiguration.class)
@ConditionalOnBean(ObjectMapper.class)
public class StarterJacksonAfterAutoConfiguration {

    @Bean
    JsonParser jsonHandler(ObjectMapper objectMapper) {
        return new JsonParser(objectMapper);
    }
}
