package com.blockchain.larisa.domain;

import com.alibaba.fastjson.annotation.JSONField;

public class CancelFailOrderInfo {

    private String orderId;

    private Integer errCode;

    private Integer errMsg;

    public String getOrderId() {
        return orderId;
    }

    @JSONField(name = "order_id")
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Integer getErrCode() {
        return errCode;
    }

    @JSONField(name = "err_code")
    public void setErrCode(Integer errCode) {
        this.errCode = errCode;
    }

    public Integer getErrMsg() {
        return errMsg;
    }

    @JSONField(name = "err_msg")
    public void setErrMsg(Integer errMsg) {
        this.errMsg = errMsg;
    }
}
