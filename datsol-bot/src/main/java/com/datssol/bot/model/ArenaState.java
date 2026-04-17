package com.datssol.bot.model;

import lombok.Data;
import java.util.List;

@Data
public class ArenaState {
    private int round;
    private int time;
    private List<Player> players;
    private List<Plantation> plantations;
}
