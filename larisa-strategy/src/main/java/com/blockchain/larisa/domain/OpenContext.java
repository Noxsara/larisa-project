package com.blockchain.larisa.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class OpenContext {

    private Integer leverRate;

    private BigDecimal contractSize;

    private BigDecimal balance;

    private BigDecimal oneSellPrice;

    private BigDecimal oneBuyPrice;

    public Integer getLeverRate() {
        return leverRate;
    }

    public void setLeverRate(Integer leverRate) {
        this.leverRate = leverRate;
    }

    public BigDecimal getContractSize() {
        return contractSize;
    }

    public void setContractSize(BigDecimal contractSize) {
        this.contractSize = contractSize;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getOneSellPrice() {
        return oneSellPrice;
    }

    public void setOneSellPrice(BigDecimal oneSellPrice) {
        this.oneSellPrice = oneSellPrice;
    }

    public BigDecimal getOneBuyPrice() {
        return oneBuyPrice;
    }

    public void setOneBuyPrice(BigDecimal oneBuyPrice) {
        this.oneBuyPrice = oneBuyPrice;
    }

    public Integer getTotalVol() {
        return (oneSellPrice.multiply(balance)
                .divide(contractSize, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(leverRate)))
                .intValue();
    }
}
