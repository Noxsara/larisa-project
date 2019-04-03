package com.blockchain.larisa.context;

import java.math.BigDecimal;

public class ProfitRate {

    private BigDecimal highRate0;

    private BigDecimal highLeaveRate0;

    private BigDecimal highRate1;

    private BigDecimal highLeaveRate1;

    private BigDecimal highRate2;

    private BigDecimal highLeaveRate2;

    public BigDecimal getHighRate0() {
        return highRate0;
    }

    public void setHighRate0(BigDecimal highRate0) {
        this.highRate0 = highRate0;
    }

    public BigDecimal getHighLeaveRate0() {
        return highLeaveRate0;
    }

    public void setHighLeaveRate0(BigDecimal highLeaveRate0) {
        this.highLeaveRate0 = highLeaveRate0;
    }

    public BigDecimal getHighRate1() {
        return highRate1;
    }

    public void setHighRate1(BigDecimal highRate1) {
        this.highRate1 = highRate1;
    }

    public BigDecimal getHighLeaveRate1() {
        return highLeaveRate1;
    }

    public void setHighLeaveRate1(BigDecimal highLeaveRate1) {
        this.highLeaveRate1 = highLeaveRate1;
    }

    public BigDecimal getHighRate2() {
        return highRate2;
    }

    public void setHighRate2(BigDecimal highRate2) {
        this.highRate2 = highRate2;
    }

    public BigDecimal getHighLeaveRate2() {
        return highLeaveRate2;
    }

    public void setHighLeaveRate2(BigDecimal highLeaveRate2) {
        this.highLeaveRate2 = highLeaveRate2;
    }

    @Override
    public String toString() {
        return "ProfitRate{" +
                "highRate0=" + highRate0 +
                ", highLeaveRate0=" + highLeaveRate0 +
                ", highRate1=" + highRate1 +
                ", highLeaveRate1=" + highLeaveRate1 +
                ", highRate2=" + highRate2 +
                ", highLeaveRate2=" + highLeaveRate2 +
                '}';
    }
}
