package com.blockchain.larisa.domain;

import com.alibaba.fastjson.annotation.JSONField;

public class OrderIdInfo {

    /**
     * 订单id
     */
    private Long orderId;

    /**
     * 客户端指定订单id
     */
    private Long clientOrderId;


    public Long getOrderId() {
        return orderId;
    }

    @JSONField(name = "order_id")
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getClientOrderId() {
        return clientOrderId;
    }

    @JSONField(name = "client_order_id")
    public void setClientOrderId(Long clientOrderId) {
        this.clientOrderId = clientOrderId;
    }
}
