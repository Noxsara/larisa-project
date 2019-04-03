package com.blockchain.larisa.domain;

import java.util.List;

public class AccountPositionResponse extends Response{

    /**
     * 持仓信息
     */
    private List<AccountPositionInfo> data;

    public List<AccountPositionInfo> getData() {
        return data;
    }

    public void setData(List<AccountPositionInfo> data) {
        this.data = data;
    }
}
