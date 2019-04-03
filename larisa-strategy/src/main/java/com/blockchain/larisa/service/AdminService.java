package com.blockchain.larisa.service;

import com.alibaba.fastjson.JSON;
import com.blockchain.larisa.common.Constants;
import com.blockchain.larisa.common.SymbolEnum;
import com.blockchain.larisa.domain.CancelOrderResponse;
import com.blockchain.larisa.domain.ContractOrderResponse;
import com.blockchain.larisa.strategy.Strategy;
import com.blockchain.larisa.strategy.StrategyFactory;
import com.blockchain.larisa.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ScheduledFuture;

@Service
public class AdminService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminService.class);

    @Autowired
    private HuobiFutureService huobiFutureService;

    public String stop(String contractName) {
        Strategy strategy = StrategyFactory.lookupStrategy(contractName);
        if (strategy == null) {
            return "找不到正在运行的策略.";
        }

        ScheduledFuture future = StrategyFactory.lookupScheduledFuture(contractName);
        if (future == null) {
            return "策略已停止.";
        }

        StrategyFactory.removeFuture(contractName);
        return strategy.stop(future);
    }

    public String resume(String contractName) {
        Strategy strategy = StrategyFactory.lookupStrategy(contractName);
        if (strategy == null) {
            return "找不到相应的策略.";
        }

        ScheduledFuture last = StrategyFactory.lookupScheduledFuture(contractName);
        if (last != null) {
            return "策略已启动.";
        }

        ScheduledFuture future = strategy.resume();
        StrategyFactory.addScheduledFuture(contractName, future);
        return Constants.TRUE;
    }

    public String reset(String contractName){
        String stopResult = stop(contractName);
        if (Constants.TRUE.equals(stopResult)) {
            return resume(contractName);
        }
        return Constants.FALSE;
    }

    public String cancelAll(String coinName) {
        CancelOrderResponse response = huobiFutureService.cancelAll(coinName);
        if (!ResponseUtil.isSuccess(response)) {
            LOGGER.error("cancel all error, response:{}", JSON.toJSONString(response));
        }
        return JSON.toJSONString(response);
    }

    public String query(String contractName, Long clientOrderId) {
        ContractOrderResponse response = huobiFutureService.contractOrder(clientOrderId, SymbolEnum.getByName(contractName));
        if (!ResponseUtil.isSuccess(response)) {
            LOGGER.info("query contract order error. response:{}", JSON.toJSONString(response));
        }
        return JSON.toJSONString(response, true);
    }
}
