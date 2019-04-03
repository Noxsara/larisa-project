package com.blockchain.larisa.domain;

import com.alibaba.fastjson.annotation.JSONField;

import java.math.BigDecimal;

public class ContractInfo {

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
     * 合约面值，即一张对应多少美元
     */
    private BigDecimal contractSize;

    /**
     * 合约价格最小变动精度
     */
    private BigDecimal priceTick;

    /**
     * 交割日
     */
    private String deliveryDate;

    /**
     * 合约上市日期
     */
    private String createDate;

    /**
     * 合约状态
     * 0:已下市、1:上市、2:待上市、3:停牌，4:暂停上市中、5:结算中、6:交割中、7:结算完成、8:交割完成、9:暂停上市
     */
    private Integer contractStatus;

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

    public BigDecimal getContractSize() {
        return contractSize;
    }

    @JSONField(name = "contract_size")
    public void setContractSize(BigDecimal contractSize) {
        this.contractSize = contractSize;
    }

    public BigDecimal getPriceTick() {
        return priceTick;
    }

    @JSONField(name = "price_tick")
    public void setPriceTick(BigDecimal priceTick) {
        this.priceTick = priceTick;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    @JSONField(name = "delivery_date")
    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getCreateDate() {
        return createDate;
    }

    @JSONField(name = "create_date")
    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public Integer getContractStatus() {
        return contractStatus;
    }

    @JSONField(name = "contract_status")
    public void setContractStatus(Integer contractStatus) {
        this.contractStatus = contractStatus;
    }
}
