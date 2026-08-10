package com.finquest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    // RestTemplate is the Spring HTTP client used to call the Flask AI microservice.
    // Declared as a bean so it is shared (one instance) and injectable anywhere.
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
