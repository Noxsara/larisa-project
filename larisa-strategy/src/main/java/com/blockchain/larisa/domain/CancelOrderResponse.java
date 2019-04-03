package com.blockchain.larisa.domain;

public class CancelOrderResponse extends Response {

    private CancelOrderInfo data;

    public CancelOrderInfo getData() {
        return data;
    }

    public void setData(CancelOrderInfo data) {
        this.data = data;
    }
}
