package com.blockchain.larisa.context;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "grid")
@Component
public class GridProperties implements StrategyProperties {

    private GridConfig eos;

    @Ignore
    private GridConfig eth;

    @Ignore
    private GridConfig btc;

    public GridConfig getEos() {
        return eos;
    }

    public void setEos(GridConfig eos) {
        this.eos = eos;
    }

    public GridConfig getEth() {
        return eth;
    }

    public void setEth(GridConfig eth) {
        this.eth = eth;
    }

    public GridConfig getBtc() {
        return btc;
    }

    public void setBtc(GridConfig btc) {
        this.btc = btc;
    }
}
