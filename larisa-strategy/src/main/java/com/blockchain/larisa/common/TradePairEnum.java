package com.blockchain.larisa.common;

import java.util.Arrays;

/**
 * 现货交易对
 */
public enum TradePairEnum {
    BTC_USDT("btcusdt", "比特币-USDT交易对"),
    ETH_USDT("ethusdt", "以太坊-USDT交易对"),
    EOS_USDT("eosusdt", "柚子-USDT交易对"),
    ;

    TradePairEnum(String pairName, String description) {
        this.pairName = pairName;
        this.description = description;
    }

    private String pairName;

    private String description;

    public String getPairName() {
        return pairName;
    }

    public String getDescription() {
        return description;
    }

    public String getCoinName() {
        return pairName.substring(0, 3);
    }

    public static TradePairEnum getByPairName(String pair) {
        return Arrays.stream(values())
                .filter(e -> e.getPairName().equals(pair))
                .findFirst()
                .orElse(null);
    }
}
