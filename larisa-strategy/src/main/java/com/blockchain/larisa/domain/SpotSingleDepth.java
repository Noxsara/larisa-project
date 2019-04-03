package com.blockchain.larisa.domain;

import java.math.BigDecimal;

/**
 * 当前买一价卖一价
 */
public class SpotSingleDepth {

    private BigDecimal oneBuyPrice;

    private BigDecimal oneSellPrice;

    public BigDecimal getOneBuyPrice() {
        return oneBuyPrice;
    }

    public void setOneBuyPrice(BigDecimal oneBuyPrice) {
        this.oneBuyPrice = oneBuyPrice;
    }

    public BigDecimal getOneSellPrice() {
        return oneSellPrice;
    }

    public void setOneSellPrice(BigDecimal oneSellPrice) {
        this.oneSellPrice = oneSellPrice;
    }

    @Override
    public String toString() {
        return "SpotSingleDepth{" +
                "oneBuyPrice=" + oneBuyPrice +
                ", oneSellPrice=" + oneSellPrice +
                '}';
    }
}
