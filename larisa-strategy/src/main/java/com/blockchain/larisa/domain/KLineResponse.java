package com.blockchain.larisa.domain;

import java.util.List;

public class KLineResponse extends Response {

    /**
     * k线数据
     */
    private List<KLine> data;

    public List<KLine> getData() {
        return data;
    }

    public void setData(List<KLine> data) {
        this.data = data;
    }
}
