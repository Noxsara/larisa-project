package com.blockchain.larisa.strategy;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.concurrent.ScheduledFuture;

public class StrategyFactory {

    /**
     * 策略合集
     */
    private static Map<String, Strategy> strategies = Maps.newConcurrentMap();

    /**
     * 策略的future合集
     */
    private static Map<String, ScheduledFuture> strategyFutures = Maps.newConcurrentMap();

    public static void addStrategy(String name, Strategy strategy) {
        strategies.put(name, strategy);
    }

    public static void addScheduledFuture(String name, ScheduledFuture scheduledFuture) {
        strategyFutures.put(name, scheduledFuture);
    }

    public static void removeFuture(String name) {
        strategyFutures.remove(name);
    }

    public static Strategy lookupStrategy(String name) {
        return strategies.get(name);
    }

    public static ScheduledFuture lookupScheduledFuture(String name) {
        return strategyFutures.get(name);
    }
}
