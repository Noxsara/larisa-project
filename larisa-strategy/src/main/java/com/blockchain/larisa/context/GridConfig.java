package com.blockchain.larisa.context;

import java.math.BigDecimal;

public class GridConfig implements StrategyConfig {

    private boolean enable;

    private String tradePair;

    private BigDecimal minPositionRate;

    private BigDecimal maxPositionRate;

    private BigDecimal waveUpperPrice;

    private BigDecimal waveLowerPrice;

    private int area;

    private BigDecimal gridMoney;

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getTradePair() {
        return tradePair;
    }

    public void setTradePair(String tradePair) {
        this.tradePair = tradePair;
    }

    public BigDecimal getMinPositionRate() {
        return minPositionRate;
    }

    public void setMinPositionRate(BigDecimal minPositionRate) {
        this.minPositionRate = minPositionRate;
    }

    public BigDecimal getMaxPositionRate() {
        return maxPositionRate;
    }

    public void setMaxPositionRate(BigDecimal maxPositionRate) {
        this.maxPositionRate = maxPositionRate;
    }

    public BigDecimal getWaveUpperPrice() {
        return waveUpperPrice;
    }

    public void setWaveUpperPrice(BigDecimal waveUpperPrice) {
        this.waveUpperPrice = waveUpperPrice;
    }

    public BigDecimal getWaveLowerPrice() {
        return waveLowerPrice;
    }

    public void setWaveLowerPrice(BigDecimal waveLowerPrice) {
        this.waveLowerPrice = waveLowerPrice;
    }

    public int getArea() {
        return area;
    }

    public void setArea(int area) {
        this.area = area;
    }

    public BigDecimal getGridMoney() {
        return gridMoney;
    }

    public void setGridMoney(BigDecimal gridMoney) {
        this.gridMoney = gridMoney;
    }

    @Override
    public boolean isEnable() {
        return enable;
    }
}
