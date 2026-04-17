package com.datssol.bot.model;

import lombok.Data;

@Data
public class Plantation {
    private String id;
    private String ownerId;
    private int x;
    private int y;
    private boolean isCapital;
    private boolean isRelay;
}
