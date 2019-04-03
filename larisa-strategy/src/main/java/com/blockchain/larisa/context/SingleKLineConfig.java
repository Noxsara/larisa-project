package com.blockchain.larisa.context;


import java.math.BigDecimal;

public class SingleKLineConfig implements StrategyConfig {

    private boolean enable;

    private String symbolName;

    private Integer leverRate;

    private BigDecimal positionRate;

    private BigDecimal riskRate;

    private ProfitRate profitRate;

    @Override
    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getSymbolName() {
        return symbolName;
    }

    public void setSymbolName(String symbolName) {
        this.symbolName = symbolName;
    }

    public Integer getLeverRate() {
        return leverRate;
    }

    public void setLeverRate(Integer leverRate) {
        this.leverRate = leverRate;
    }

    public BigDecimal getPositionRate() {
        return positionRate;
    }

    public void setPositionRate(BigDecimal positionRate) {
        this.positionRate = positionRate;
    }

    public BigDecimal getRiskRate() {
        return riskRate;
    }

    public void setRiskRate(BigDecimal riskRate) {
        this.riskRate = riskRate;
    }

    public ProfitRate getProfitRate() {
        return profitRate;
    }

    public void setProfitRate(ProfitRate profitRate) {
        this.profitRate = profitRate;
    }
}
