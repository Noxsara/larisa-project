package com.blockchain.larisa.common;

public enum OrderStatusEnum {
    PREPARE_COMMIT(1, "准备提交"),
    PREPARE_COMMIT_2(2, "准备提交"),
    ALREADY_COMMITTED(3, "已提交"),
    PART_TRADE(4, "部分成交"),
    PART_TRADE_WITH_CANCEL(5, "部分成交已撤单"),
    ALL_TRADE(6, "全部成交"),
    ALREADY_CANCEL(7, "已撤单"),
    CANCELING(11, "撤单中"),;

    OrderStatusEnum(Integer status, String description) {
        this.status = status;
        this.description = description;
    }

    private Integer status;

    private String description;

    public Integer getStatus() {
        return status;
    }


    public String getDescription() {
        return description;
    }

}
