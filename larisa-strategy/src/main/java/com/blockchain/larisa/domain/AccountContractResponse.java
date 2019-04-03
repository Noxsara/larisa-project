package com.blockchain.larisa.domain;

import java.util.List;

public class AccountContractResponse extends Response {

    /**
     * 账户信息
     */
    private List<AccountContractInfo> data;

    public List<AccountContractInfo> getData() {
        return data;
    }

    public void setData(List<AccountContractInfo> data) {
        this.data = data;
    }
}
