package com.blockchain.larisa.context;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "singlekline")
@Component
public class SingleKLineProperties implements StrategyProperties{

    private SingleKLineConfig eos;

    private SingleKLineConfig eth;

    @Ignore
    private SingleKLineConfig btc;

    public SingleKLineConfig getEos() {
        return eos;
    }

    public void setEos(SingleKLineConfig eos) {
        this.eos = eos;
    }

    public SingleKLineConfig getEth() {
        return eth;
    }

    public void setEth(SingleKLineConfig eth) {
        this.eth = eth;
    }

    public SingleKLineConfig getBtc() {
        return btc;
    }

    public void setBtc(SingleKLineConfig btc) {
        this.btc = btc;
    }
}
