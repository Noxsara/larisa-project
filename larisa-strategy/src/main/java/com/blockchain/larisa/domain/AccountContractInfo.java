package com.blockchain.larisa.domain;

import com.alibaba.fastjson.annotation.JSONField;

import java.math.BigDecimal;

public class AccountContractInfo {

    /**
     * 品种代码
     */
    private String symbol;

    /**
     * 账户权益
     */
    private BigDecimal marginBalance;

    /**
     * 持仓保证金（当前持有仓位所占用的保证金）
     */
    private BigDecimal marginPosition;

    /**
     * 冻结保证金
     */
    private BigDecimal marginFrozen;

    /**
     * 可用保证金
     */
    private BigDecimal marginAvailable;

    /**
     * 已实现盈亏
     */
    private BigDecimal profitReal;

    /**
     * 未实现盈亏
     */
    private BigDecimal profitUnreal;

    /**
     * 保证金率
     */
    private BigDecimal riskRate;

    /**
     * 预估强平价
     */
    private BigDecimal liquidationPrice;

    /**
     * 可划转数
     */
    private BigDecimal withdrawAvailable;

    /**
     * 杠杠倍数
     */
    private Integer leverRate;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public BigDecimal getMarginBalance() {
        return marginBalance;
    }

    @JSONField(name = "margin_balance")
    public void setMarginBalance(BigDecimal marginBalance) {
        this.marginBalance = marginBalance;
    }

    public BigDecimal getMarginPosition() {
        return marginPosition;
    }

    @JSONField(name = "margin_position")
    public void setMarginPosition(BigDecimal marginPosition) {
        this.marginPosition = marginPosition;
    }

    public BigDecimal getMarginFrozen() {
        return marginFrozen;
    }

    @JSONField(name = "margin_frozen")
    public void setMarginFrozen(BigDecimal marginFrozen) {
        this.marginFrozen = marginFrozen;
    }

    public BigDecimal getMarginAvailable() {
        return marginAvailable;
    }

    @JSONField(name = "margin_available")
    public void setMarginAvailable(BigDecimal marginAvailable) {
        this.marginAvailable = marginAvailable;
    }

    public BigDecimal getProfitReal() {
        return profitReal;
    }

    @JSONField(name = "profit_real")
    public void setProfitReal(BigDecimal profitReal) {
        this.profitReal = profitReal;
    }

    public BigDecimal getProfitUnreal() {
        return profitUnreal;
    }

    @JSONField(name = "profit_unreal")
    public void setProfitUnreal(BigDecimal profitUnreal) {
        this.profitUnreal = profitUnreal;
    }

    public BigDecimal getRiskRate() {
        return riskRate;
    }

    @JSONField(name = "risk_rate")
    public void setRiskRate(BigDecimal riskRate) {
        this.riskRate = riskRate;
    }

    public BigDecimal getLiquidationPrice() {
        return liquidationPrice;
    }

    @JSONField(name = "liquidation_price")
    public void setLiquidationPrice(BigDecimal liquidationPrice) {
        this.liquidationPrice = liquidationPrice;
    }

    public BigDecimal getWithdrawAvailable() {
        return withdrawAvailable;
    }

    @JSONField(name = "withdraw_available")
    public void setWithdrawAvailable(BigDecimal withdrawAvailable) {
        this.withdrawAvailable = withdrawAvailable;
    }

    public Integer getLeverRate() {
        return leverRate;
    }

    @JSONField(name = "lever_rate")
    public void setLeverRate(Integer leverRate) {
        this.leverRate = leverRate;
    }
}
