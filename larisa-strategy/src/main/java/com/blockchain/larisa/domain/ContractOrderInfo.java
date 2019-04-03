package com.blockchain.larisa.domain;

import com.alibaba.fastjson.annotation.JSONField;

import java.math.BigDecimal;

public class ContractOrderInfo {

    /**
     * 品种代码
     */
    private String symbol;

    /**
     * 合约类型
     */
    private String contractType;

    /**
     * 合约代码
     */
    private String contractCode;

    /**
     * 委托数量
     */
    private BigDecimal volume;

    /**
     * 委托价格
     */
    private BigDecimal price;

    /**
     * 订单报价类型 "limit":限价 "opponent":对手价
     */
    private String orderPriceType;

    /**
     * "buy":买 "sell":卖
     */
    private String direction;

    /**
     * "open":开 "close":平
     */
    private String offset;

    /**
     * 杠杆倍数
     */
    private Integer leverRate;

    /**
     * 订单id
     */
    private Long orderId;

    /**
     * 客户端订单id
     */
    private Long clientOrderId;

    /**
     * 成交实际
     */
    private Long createdTime;

    /**
     * 成交数量
     */
    private BigDecimal tradeVolume;

    /**
     * 成交总金额
     */
    private BigDecimal tradeTurnover;

    /**
     * 手续费
     */
    private BigDecimal fee;

    /**
     * 平均成交价
     */
    private BigDecimal tradeAvgPrice;

    /**
     * 冻结保证金
     */
    private BigDecimal marginFrozen;

    /**
     * 收益
     */
    private BigDecimal profit;

    /**
     * 订单状态
     */
    private Integer status;

    /**
     * 订单类型
     */
    private String orderType;

    /**
     * 订单来源
     */
    private String orderSource;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getContractType() {
        return contractType;
    }

    @JSONField(name = "contract_type")
    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    public String getContractCode() {
        return contractCode;
    }

    @JSONField(name = "contract_code")
    public void setContractCode(String contractCode) {
        this.contractCode = contractCode;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getOrderPriceType() {
        return orderPriceType;
    }

    @JSONField(name = "order_price_type")
    public void setOrderPriceType(String orderPriceType) {
        this.orderPriceType = orderPriceType;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public Integer getLeverRate() {
        return leverRate;
    }

    @JSONField(name = "lever_rate")
    public void setLeverRate(Integer leverRate) {
        this.leverRate = leverRate;
    }

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

    public Long getCreatedTime() {
        return createdTime;
    }

    @JSONField(name = "created_at")
    public void setCreatedTime(Long createdTime) {
        this.createdTime = createdTime;
    }

    public BigDecimal getTradeVolume() {
        return tradeVolume;
    }

    @JSONField(name = "trade_volume")
    public void setTradeVolume(BigDecimal tradeVolume) {
        this.tradeVolume = tradeVolume;
    }

    public BigDecimal getTradeTurnover() {
        return tradeTurnover;
    }

    @JSONField(name = "trade_turnover")
    public void setTradeTurnover(BigDecimal tradeTurnover) {
        this.tradeTurnover = tradeTurnover;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public BigDecimal getTradeAvgPrice() {
        return tradeAvgPrice;
    }

    @JSONField(name = "trade_avg_price")
    public void setTradeAvgPrice(BigDecimal tradeAvgPrice) {
        this.tradeAvgPrice = tradeAvgPrice;
    }

    public BigDecimal getMarginFrozen() {
        return marginFrozen;
    }

    @JSONField(name = "margin_frozen")
    public void setMarginFrozen(BigDecimal marginFrozen) {
        this.marginFrozen = marginFrozen;
    }

    public BigDecimal getProfit() {
        return profit;
    }

    public void setProfit(BigDecimal profit) {
        this.profit = profit;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getOrderType() {
        return orderType;
    }

    @JSONField(name = "order_type")
    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getOrderSource() {
        return orderSource;
    }

    @JSONField(name = "order_source")
    public void setOrderSource(String orderSource) {
        this.orderSource = orderSource;
    }
}
