package com.blockchain.larisa.common;

public enum  OffsetEnum {
    OPEN("open", "看多"),
    CLOSE("close", "看空"),

    ;

    OffsetEnum(String name, String description) {
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
