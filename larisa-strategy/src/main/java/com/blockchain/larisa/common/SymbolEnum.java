package com.blockchain.larisa.common;

import java.util.Arrays;

public enum SymbolEnum {

    BTC_CW("BTC_CW", "this_week", "BTC当周合约"),
    BTC_NW("BTC_NW", "next_week", "BTC次周合约"),
    BTC_CQ("BTC_CQ", "quarter", "BTC季度合约"),
    ETH_CW("ETH_CW", "this_week", "ETH当周合约"),
    ETH_NW("ETH_NW", "next_week", "ETH次周合约"),
    ETH_CQ("ETH_CQ", "quarter", "ETH季度合约"),
    EOS_CW("EOS_CW", "this_week", "EOS当周合约"),
    EOS_NW("EOS_NW", "next_week", "EOS次周合约"),
    EOS_CQ("EOS_CQ", "quarter", "EOS季度合约"),
    ;


    SymbolEnum(String name, String contractType, String description) {
        this.name = name;
        this.contractType = contractType;
        this.description = description;
    }

    private String name;
    private String contractType;
    private String description;

    public String getName() {
        return name;
    }

    public String getContractType() {
        return contractType;
    }

    public String getDescription() {
        return description;
    }

    public String getCoinName() {
        String name = getName();
        return name.substring(0, name.indexOf("_"));
    }

    public static SymbolEnum getByName(String name) {
        SymbolEnum symbol = Arrays.stream(SymbolEnum.values())
                .filter(e -> e.getName().contains(name))
                .findFirst()
                .orElse(null);
        if (symbol == null) {
            throw new RuntimeException(String.format("name:%s, 找不到对应的symbol.", name));
        }

        return symbol;
    }
}
