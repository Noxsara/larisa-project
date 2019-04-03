package com.blockchain.larisa.domain;

import java.math.BigDecimal;

public class SpotBalance {

    private BigDecimal blance;

    private BigDecimal frozen;

    public BigDecimal getBlance() {
        return blance;
    }

    public void setBlance(BigDecimal blance) {
        this.blance = blance;
    }

    public BigDecimal getFrozen() {
        return frozen;
    }

    public void setFrozen(BigDecimal frozen) {
        this.frozen = frozen;
    }

    @Override
    public String toString() {
        return "SpotBalance{" +
                "blance=" + blance +
                ", frozen=" + frozen +
                '}';
    }
}
