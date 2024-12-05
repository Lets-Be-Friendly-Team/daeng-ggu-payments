package com.ureca.daengggupayments.config.webClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${PSP.toss.secret-key}")
    private String tossSecretKey;

    @Value("${PSP.toss.url}")
    private String tossUrl;

    @Bean
    public WebClient tossPaymentsWebClient() {
        String basicAuthHeader = "Basic " + encodeToBase64(tossSecretKey + ":");

        return WebClient.builder()
                .baseUrl(tossUrl)
                .defaultHeader("Authorization", basicAuthHeader)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    private String encodeToBase64(String value) {
        return java.util.Base64.getEncoder().encodeToString(value.getBytes());
    }
}
