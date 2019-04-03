package com.blockchain.larisa.domain;

import com.alibaba.fastjson.annotation.JSONField;

import java.math.BigDecimal;

public class AccountPositionInfo {

    /**
     * 品种代码
     */
    private String symbol;

    /**
     * 合约代码
     */
    private String contractCode;

    /**
     * 合约类型
     */
    private String contractType;

    /**
     * 持仓量
     */
    private BigDecimal volume;

    /**
     * 可平仓数量
     */
    private BigDecimal available;

    /**
     * 冻结数量
     */
    private BigDecimal frozen;

    /**
     * 开仓均价
     */
    private BigDecimal costOpen;

    /**
     * 持仓均价
     */
    private BigDecimal costHold;

    /**
     * 未实现盈亏
     */
    private BigDecimal profitUnreal;

    /**
     * 收益率
     */
    private BigDecimal profitRate;

    /**
     * 收益
     */
    private BigDecimal profit;

    /**
     * 持仓保证金
     */
    private BigDecimal positionMargin;

    /**
     * 杠杠倍数
     */
    private Integer leverRate;

    /**
     * "buy":买 "sell":卖
     */
    private String direction;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getContractCode() {
        return contractCode;
    }

    @JSONField(name = "contract_code")
    public void setContractCode(String contractCode) {
        this.contractCode = contractCode;
    }

    public String getContractType() {
        return contractType;
    }

    @JSONField(name = "contract_type")
    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public BigDecimal getAvailable() {
        return available;
    }

    public void setAvailable(BigDecimal available) {
        this.available = available;
    }

    public BigDecimal getFrozen() {
        return frozen;
    }

    public void setFrozen(BigDecimal frozen) {
        this.frozen = frozen;
    }

    public BigDecimal getCostOpen() {
        return costOpen;
    }

    @JSONField(name = "cost_open")
    public void setCostOpen(BigDecimal costOpen) {
        this.costOpen = costOpen;
    }

    public BigDecimal getCostHold() {
        return costHold;
    }

    @JSONField(name = "cost_hold")
    public void setCostHold(BigDecimal costHold) {
        this.costHold = costHold;
    }

    public BigDecimal getProfitUnreal() {
        return profitUnreal;
    }

    @JSONField(name = "profit_unreal")
    public void setProfitUnreal(BigDecimal profitUnreal) {
        this.profitUnreal = profitUnreal;
    }

    public BigDecimal getProfitRate() {
        return profitRate;
    }

    @JSONField(name = "profit_rate")
    public void setProfitRate(BigDecimal profitRate) {
        this.profitRate = profitRate;
    }

    public BigDecimal getProfit() {
        return profit;
    }

    public void setProfit(BigDecimal profit) {
        this.profit = profit;
    }

    public BigDecimal getPositionMargin() {
        return positionMargin;
    }

    @JSONField(name = "position_margin")
    public void setPositionMargin(BigDecimal positionMargin) {
        this.positionMargin = positionMargin;
    }

    public Integer getLeverRate() {
        return leverRate;
    }

    @JSONField(name = "lever_rate")
    public void setLeverRate(Integer leverRate) {
        this.leverRate = leverRate;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}
