package com.blockchain.larisa.domain;

public class DepthResponse extends Response{

    /**
     * 深度详情
     */
    private Tick tick;

    public Tick getTick() {
        return tick;
    }

    public void setTick(Tick tick) {
        this.tick = tick;
    }
}
