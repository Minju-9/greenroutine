package com.example.greenroutine.config;

import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;


import java.time.Duration;

@Configuration
@ConditionalOnProperty(prefix = "gemini", name = "enabled", havingValue = "true")
public class GeminiWebClient {
    @Bean
    public WebClient geminiWebClient(@Value("${gemini.timeout-ms:4000}") long timeoutMs) {
        HttpClient http = HttpClient.create()
                .responseTimeout(Duration.ofMillis(timeoutMs))
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler((int) timeoutMs / 1000))
                                .addHandlerLast(new WriteTimeoutHandler((int) timeoutMs / 1000))
                );

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(http))
                .build();
    }
}
