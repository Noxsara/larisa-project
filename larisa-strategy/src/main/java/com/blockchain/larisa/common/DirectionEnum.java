package com.blockchain.larisa.common;

public enum DirectionEnum {
    BUY("buy", "看多"),
    SELL("sell", "看空"),;

    DirectionEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    private String name;

    private String description;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
