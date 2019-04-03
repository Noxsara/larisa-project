package com.blockchain.larisa.domain;


import java.math.BigDecimal;

public class SpotOrderDetail {

    /**
     * 订单指定数量(买为法币的数量, 卖为币的数量)
     */
    private BigDecimal orderAmount;

    /**
     * 指定价格。如果以市场价成交, 则price=0
     */
    private BigDecimal price;

    /**
     * 订单类型
     * @see com.blockchain.larisa.huobi.spot.request.CreateOrderRequest.OrderType
     */
    private String type;

    /**
     * 已成交数量
     */
    private BigDecimal dealAmount;

    /**
     * 已成交金额
     */
    private BigDecimal dealCash;

    /**
     * 订单状态
     */
    private String state;

    public BigDecimal getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(BigDecimal orderAmount) {
        this.orderAmount = orderAmount;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getDealAmount() {
        return dealAmount;
    }

    public void setDealAmount(BigDecimal dealAmount) {
        this.dealAmount = dealAmount;
    }

    public BigDecimal getDealCash() {
        return dealCash;
    }

    public void setDealCash(BigDecimal dealCash) {
        this.dealCash = dealCash;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "SpotOrderDetail{" +
                "orderAmount=" + orderAmount +
                ", price=" + price +
                ", type='" + type + '\'' +
                ", dealAmount=" + dealAmount +
                ", dealCash=" + dealCash +
                ", state='" + state + '\'' +
                '}';
    }
}
