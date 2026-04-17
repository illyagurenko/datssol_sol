package com.datssol.bot.strategy;

import com.datssol.bot.model.*;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

@Component
public class GameStrategy {

    private static final int EXPANSION_PHASE_END = 150;
    private static final int CONSOLIDATION_PHASE_END = 450;
    private static final int GAME_END = 600;

    public CommandRequest decideCommand(ArenaState state, String playerId) {
        int time = state.getTime();
        GamePhase phase = getGamePhase(time);

        Player myPlayer = findPlayer(state.getPlayers(), playerId);
        if (myPlayer == null) {
            return new CommandRequest("wait", 0, 0);
        }

        List<Plantation> myPlantations = findMyPlantations(state.getPlantations(), playerId);
        Plantation capital = findCapital(myPlantations);

        switch (phase) {
            case EXPANSION:
                return expansionStrategy(state, playerId, myPlayer, capital);
            case CONSOLIDATION:
                return consolidationStrategy(state, playerId, myPlayer, myPlantations, capital);
            case ENDGAME:
                return endgameStrategy(state, playerId, myPlayer, myPlantations, capital);
            default:
                return new CommandRequest("wait", 0, 0);
        }
    }

    private GamePhase getGamePhase(int time) {
        if (time < EXPANSION_PHASE_END) {
            return GamePhase.EXPANSION;
        } else if (time < CONSOLIDATION_PHASE_END) {
            return GamePhase.CONSOLIDATION;
        } else {
            return GamePhase.ENDGAME;
        }
    }

    private Player findPlayer(List<Player> players, String playerId) {
        return players.stream()
                .filter(p -> p.getId().equals(playerId))
                .findFirst()
                .orElse(null);
    }

    private List<Plantation> findMyPlantations(List<Plantation> plantations, String playerId) {
        return plantations.stream()
                .filter(p -> p.getOwnerId() != null && p.getOwnerId().equals(playerId))
                .toList();
    }

    private Plantation findCapital(List<Plantation> plantations) {
        return plantations.stream()
                .filter(Plantation::isCapital)
                .findFirst()
                .orElse(null);
    }

    private CommandRequest expansionStrategy(ArenaState state, String playerId, 
                                              Player myPlayer, Plantation capital) {
        if (capital == null) {
            return new CommandRequest("wait", 0, 0);
        }

        if (myPlayer.getCommands() >= 3) {
            int newX = capital.getX() + 2;
            int newY = capital.getY();
            return new CommandRequest("create", newX, newY);
        }

        return new CommandRequest("wait", 0, 0);
    }

    private CommandRequest consolidationStrategy(ArenaState state, String playerId,
                                                  Player myPlayer, List<Plantation> myPlantations,
                                                  Plantation capital) {
        if (capital == null || myPlantations.isEmpty()) {
            return new CommandRequest("wait", 0, 0);
        }

        Optional<Plantation> threatenedCapital = checkCapitalThreat(state, playerId, capital);
        if (threatenedCapital.isPresent()) {
            return defendCapital(capital);
        }

        if (myPlayer.getCommands() >= 2) {
            Plantation frontier = findFrontierPlantation(myPlantations, capital);
            if (frontier != null) {
                int newX = frontier.getX() + (frontier.getX() - capital.getX()) / Math.max(1, 
                    Math.abs(frontier.getX() - capital.getX()) + Math.abs(frontier.getY() - capital.getY()));
                int newY = frontier.getY() + (frontier.getY() - capital.getY()) / Math.max(1,
                    Math.abs(frontier.getX() - capital.getX()) + Math.abs(frontier.getY() - capital.getY()));
                return new CommandRequest("create", newX, newY);
            }
        }

        return new CommandRequest("wait", 0, 0);
    }

    private CommandRequest endgameStrategy(ArenaState state, String playerId,
                                            Player myPlayer, List<Plantation> myPlantations,
                                            Plantation capital) {
        Optional<Plantation> threatenedCapital = checkCapitalThreat(state, playerId, capital);
        if (threatenedCapital.isPresent()) {
            return defendCapital(capital);
        }

        return new CommandRequest("wait", 0, 0);
    }

    private Optional<Plantation> checkCapitalThreat(ArenaState state, String playerId, Plantation capital) {
        if (capital == null) {
            return Optional.empty();
        }

        for (Plantation p : state.getPlantations()) {
            if (p.getOwnerId() != null && !p.getOwnerId().equals(playerId)) {
                int distance = Math.abs(p.getX() - capital.getX()) + Math.abs(p.getY() - capital.getY());
                if (distance <= 2) {
                    return Optional.of(p);
                }
            }
        }
        return Optional.empty();
    }

    private CommandRequest defendCapital(Plantation capital) {
        return new CommandRequest("wait", 0, 0);
    }

    private Plantation findFrontierPlantation(List<Plantation> plantations, Plantation capital) {
        return plantations.stream()
                .max((p1, p2) -> {
                    int d1 = Math.abs(p1.getX() - capital.getX()) + Math.abs(p1.getY() - capital.getY());
                    int d2 = Math.abs(p2.getX() - capital.getX()) + Math.abs(p2.getY() - capital.getY());
                    return Integer.compare(d1, d2);
                })
                .orElse(null);
    }

    private enum GamePhase {
        EXPANSION, CONSOLIDATION, ENDGAME
    }
}
