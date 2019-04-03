package com.blockchain.larisa.common;

public enum KLineEnum {

    ONE_MINUTE("1min", "1分钟K线"),
    FIVE_MINUTE("5min", "5分钟K线"),
    FIFTEEN_MINUTE("15min", "15分钟K线"),
    THIRTY_MINUTE("30min", "30分钟K线"),
    SIXTY_MINUTE("60min", "60分钟K线"),
    FOUR_HOUR("4hour", "4小时K线"),
    ONE_DAY("1day", "日K线"),
    ONE_MONTH("1mon", "月K线"),

    ;

    KLineEnum(String name, String description) {
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
