package com.datssol.bot.service;

import com.datssol.bot.client.DatsSolClient;
import com.datssol.bot.model.ArenaState;
import com.datssol.bot.model.CommandRequest;
import com.datssol.bot.strategy.GameStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class GameService {

    private static final Logger log = LoggerFactory.getLogger(GameService.class);

    private final DatsSolClient client;
    private final GameStrategy strategy;

    @Value("${datssol.player-id:}")
    private String playerId;

    public GameService(DatsSolClient client, GameStrategy strategy) {
        this.client = client;
        this.strategy = strategy;
    }

    @Scheduled(fixedRate = 1000)
    public void gameLoop() {
        if (playerId == null || playerId.isEmpty()) {
            log.warn("Player ID not configured");
            return;
        }

        client.getArenaState()
                .flatMap(state -> {
                    log.debug("Arena state: round={}, time={}", state.getRound(), state.getTime());
                    CommandRequest command = strategy.decideCommand(state, playerId);
                    log.info("Decided command: {} at ({},{})", command.getCommand(), command.getX(), command.getY());
                    return client.sendCommand(command);
                })
                .doOnNext(response -> log.info("Command response: {}", response))
                .onErrorResume(e -> {
                    log.error("Error in game loop", e);
                    return Mono.empty();
                })
                .subscribe();
    }
}
