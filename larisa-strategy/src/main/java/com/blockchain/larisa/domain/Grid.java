package com.blockchain.larisa.domain;

import java.math.BigDecimal;

//一个网格
public class Grid {

    private BigDecimal lowerPrice;

    private BigDecimal upperPrice;

    //上次成交价格
    private BigDecimal gridDealPrice;

    //网格序号
    private int index;

    public BigDecimal getLowerPrice() {
        return lowerPrice;
    }

    public void setLowerPrice(BigDecimal lowerPrice) {
        this.lowerPrice = lowerPrice;
    }

    public BigDecimal getUpperPrice() {
        return upperPrice;
    }

    public void setUpperPrice(BigDecimal upperPrice) {
        this.upperPrice = upperPrice;
    }

    public BigDecimal getGridDealPrice() {
        return gridDealPrice;
    }

    public void setGridDealPrice(BigDecimal gridDealPrice) {
        this.gridDealPrice = gridDealPrice;
    }


    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "Grid{" +
                "lowerPrice=" + lowerPrice +
                ", upperPrice=" + upperPrice +
                ", gridDealPrice=" + gridDealPrice +
                ", index=" + index +
                '}';
    }
}
