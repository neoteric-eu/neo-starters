package neostarter.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class SpringMVCSampleApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(SpringMVCSampleApplication.class, args);
    }

}
