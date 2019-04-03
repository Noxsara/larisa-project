package com.blockchain.larisa.common;

public enum PriceTypeEnum {

    LIMIT("limit", "限价"),
    OPPONENT("opponent", "对手价"),

    ;

    PriceTypeEnum(String name, String description) {
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
