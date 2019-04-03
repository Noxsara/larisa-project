package com.blockchain.larisa.launcher;

import com.alibaba.fastjson.JSON;
import com.blockchain.larisa.context.Ignore;
import com.blockchain.larisa.context.StrategyConfig;
import com.blockchain.larisa.context.StrategyProperties;
import com.blockchain.larisa.strategy.Strategy;
import com.blockchain.larisa.strategy.StrategyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.concurrent.ScheduledFuture;


public abstract class AbstractStrategyLauncher implements StrategyLauncher {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractStrategyLauncher.class);

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void afterPropertiesSet() {
        StrategyProperties properties = getProperties();
        Field[] fields = properties.getClass().getDeclaredFields();
        Arrays.stream(fields).forEach(field -> {
            try {
                Ignore ignore = field.getAnnotation(Ignore.class);
                if (ignore != null) {
                    return;
                }

                field.setAccessible(true);
                StrategyConfig config = (StrategyConfig) field.get(properties);
                if (!config.isEnable()) {
                    return;
                }
                Strategy strategy = (Strategy) applicationContext.getBean(strategyClass());
                ScheduledFuture scheduledFuture = strategy.start(config);
                StrategyFactory.addStrategy(strategy.name(), strategy);
                StrategyFactory.addScheduledFuture(strategy.name(), scheduledFuture);
            } catch (Exception e) {
                LOGGER.error("AbstractStrategyLauncher launch error. properties:{}, strategy:{}, e:{}",
                        JSON.toJSONString(properties), strategyClass().getSimpleName(), e);
            }
        });
    }

    protected abstract StrategyProperties getProperties();

    protected abstract Class strategyClass();
}
