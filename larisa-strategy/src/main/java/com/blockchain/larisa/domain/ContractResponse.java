package com.blockchain.larisa.domain;

import java.util.List;

public class ContractResponse extends Response {

    /**
     * 合约信息
     */
    private List<ContractInfo> data;

    public List<ContractInfo> getData() {
        return data;
    }

    public void setData(List<ContractInfo> data) {
        this.data = data;
    }
}
