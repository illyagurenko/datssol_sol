package com.datssol.bot.client;

import com.datssol.bot.model.ArenaState;
import com.datssol.bot.model.CommandRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class DatsSolClient {

    private final WebClient webClient;

    public DatsSolClient(@Value("${datssol.api.base-url}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public Mono<ArenaState> getArenaState() {
        return webClient.get()
                .uri("/static/datssol/openapi/api/arena")
                .retrieve()
                .bodyToMono(ArenaState.class);
    }

    public Mono<String> sendCommand(CommandRequest command) {
        return webClient.post()
                .uri("/static/datssol/openapi/api/command")
                .bodyValue(command)
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<String> getLogs() {
        return webClient.get()
                .uri("/static/datssol/openapi/api/logs")
                .retrieve()
                .bodyToMono(String.class);
    }
}
