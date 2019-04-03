package com.blockchain.larisa.domain;

import java.util.List;

public class Tick {

    /**
     * 消息id
     */
    private Long id;

    /**
     * 渠道
     */
    private String ch;

    /**
     * 订单id
     */
    private String mrid;

    /**
     * 买盘
     */
    private List<Post> bids;

    /**
     * 卖盘
     */
    private List<Post> asks;

    /**
     * 时间戳
     */
    private Long ts;

    /**
     * 版本
     */
    private String version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTs() {
        return ts;
    }

    public void setTs(Long ts) {
        this.ts = ts;
    }

    public String getCh() {
        return ch;
    }

    public void setCh(String ch) {
        this.ch = ch;
    }

    public String getMrid() {
        return mrid;
    }

    public void setMrid(String mrid) {
        this.mrid = mrid;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<Post> getBids() {
        return bids;
    }

    public void setBids(List<Post> bids) {
        this.bids = bids;
    }

    public List<Post> getAsks() {
        return asks;
    }

    public void setAsks(List<Post> asks) {
        this.asks = asks;
    }
}
