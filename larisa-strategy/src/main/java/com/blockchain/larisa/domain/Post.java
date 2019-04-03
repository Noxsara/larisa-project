package com.blockchain.larisa.domain;

import java.math.BigDecimal;
import java.util.ArrayList;

public class Post extends ArrayList<BigDecimal> {

    /**
     * 获取挂单价
     */
    public BigDecimal getPrice() {
        return get(0);
    }

    /**
     * 获取挂单张数
     */
    public Integer getVol() {
        return get(1).intValue();
    }

}
