package com.blockchain.larisa.domain;


public class OrderResponse extends Response {

    /**
     * 订单id信息
     */
    private OrderIdInfo data;

    public OrderIdInfo getData() {
        return data;
    }

    public void setData(OrderIdInfo data) {
        this.data = data;
    }

}
