package com.blockchain.larisa.launcher;

import com.blockchain.larisa.context.GridProperties;
import com.blockchain.larisa.context.StrategyProperties;
import com.blockchain.larisa.strategy.GridStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GridStrategyLauncher extends AbstractStrategyLauncher {

    @Autowired
    private GridProperties gridProperties;

    @Override
    protected StrategyProperties getProperties() {
        return gridProperties;
    }

    @Override
    protected Class strategyClass() {
        return GridStrategy.class;
    }
}
