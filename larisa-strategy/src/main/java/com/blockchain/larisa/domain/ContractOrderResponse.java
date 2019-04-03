package com.blockchain.larisa.domain;

import java.util.List;

public class ContractOrderResponse extends Response {

    /**
     * 合约订单详情
     */
    private List<ContractOrderInfo > data;

    public List<ContractOrderInfo> getData() {
        return data;
    }

    public void setData(List<ContractOrderInfo> data) {
        this.data = data;
    }
}
