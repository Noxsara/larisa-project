package com.blockchain.larisa.launcher;

import com.blockchain.larisa.context.SingleKLineProperties;
import com.blockchain.larisa.context.StrategyProperties;
import com.blockchain.larisa.strategy.SingleKLineStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SingleKLineStrategyLauncher extends AbstractStrategyLauncher{

    @Autowired
    private SingleKLineProperties singleKLineProperties;

    @Override
    protected StrategyProperties getProperties() {
        return singleKLineProperties;
    }

    @Override
    protected Class strategyClass() {
        return SingleKLineStrategy.class;
    }

}
