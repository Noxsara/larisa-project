package com.blockchain.larisa.strategy;


import com.blockchain.larisa.context.StrategyConfig;

import java.util.concurrent.ScheduledFuture;

/**
 * 策略生命周期：start->resume->run->stop
 */
public interface Strategy{

    /**
     * 启动策略
     */
    ScheduledFuture start(StrategyConfig config);

    /**
     * 恢复策略
     */
    ScheduledFuture resume();

    /**
     * 执行策略
     */
    void run();

    /**
     * 停止策略
     */
    String stop(ScheduledFuture scheduledFuture);

    String name();
}