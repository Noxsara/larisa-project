package com.blockchain.larisa.common;

public enum  TrendEnum {

    RISE("rise", "涨"),
    FALL("fall", "跌"),

    ;


    private String trend;

    private String description;

    TrendEnum(String trend, String description) {
        this.trend = trend;
        this.description = description;
    }

    public String getTrend() {
        return trend;
    }

    public String getDescription() {
        return description;
    }

}
