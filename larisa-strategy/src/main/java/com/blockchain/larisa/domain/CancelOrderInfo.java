package com.blockchain.larisa.domain;

import java.util.List;

public class CancelOrderInfo {

    /**
     * 取消失败的订单
     */
    private List<CancelFailOrderInfo> errors;

    /**
     * 取消成功的订单
     */
    private List<String> successes;

    public List<CancelFailOrderInfo> getErrors() {
        return errors;
    }

    public void setErrors(List<CancelFailOrderInfo> errors) {
        this.errors = errors;
    }

    public List<String> getSuccesses() {
        return successes;
    }

    public void setSuccesses(List<String> successes) {
        this.successes = successes;
    }
}
