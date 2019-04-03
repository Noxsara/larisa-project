package com.blockchain.larisa.service;

import com.alibaba.fastjson.JSON;

import com.blockchain.larisa.common.DirectionEnum;
import com.blockchain.larisa.common.KLineEnum;
import com.blockchain.larisa.common.MergeDepthEnum;
import com.blockchain.larisa.common.OffsetEnum;
import com.blockchain.larisa.common.PriceTypeEnum;
import com.blockchain.larisa.common.SymbolEnum;
import com.blockchain.larisa.domain.AccountContractResponse;
import com.blockchain.larisa.domain.AccountPositionResponse;
import com.blockchain.larisa.domain.CancelOrderResponse;
import com.blockchain.larisa.domain.ContractOrderResponse;
import com.blockchain.larisa.domain.ContractResponse;
import com.blockchain.larisa.domain.DepthResponse;
import com.blockchain.larisa.domain.KLineResponse;
import com.blockchain.larisa.domain.OrderResponse;
import com.blockchain.larisa.huobi.future.api.HbdmRestApiV1;
import com.blockchain.larisa.huobi.future.api.IHbdmRestApi;
import com.blockchain.larisa.util.OrderIdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class HuobiFutureService implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(HuobiFutureService.class);

    @Value("${future.access.key}")
    private String apiKey;

    @Value("${future.secret.key}")
    private String secretKey;

    @Value("${huobi.dm.url}")
    private String url;

    private IHbdmRestApi futureAPI;

    @Override
    public void afterPropertiesSet() {
        futureAPI = new HbdmRestApiV1(url, apiKey, secretKey);
    }

    public ContractResponse contract(SymbolEnum symbol) {
        try {
            String response = futureAPI.futureContractInfo(symbol.getCoinName(), symbol.getContractType(), null);
            return JSON.parseObject(response, ContractResponse.class);
        } catch (Exception e) {
            LOGGER.error("get contract error. exception:{}", e);
        }
        return null;
    }

    public DepthResponse depth(SymbolEnum symbol, MergeDepthEnum mergeDepth) {
        try {
            String response = futureAPI.futureMarketDepth(symbol.getName(), mergeDepth.getName());
            return JSON.parseObject(response, DepthResponse.class);
        } catch (Exception e) {
            LOGGER.error("get depth error. exception:{}", e);
        }
        return null;
    }

    public KLineResponse kline(SymbolEnum symbol, KLineEnum kLineType, String size) {
        try {
            String response = futureAPI.futureMarketHistoryKline(symbol.getName(), kLineType.getName(), size);
            return JSON.parseObject(response, KLineResponse.class);
        } catch (Exception e) {
            LOGGER.error("get kline history error. exception:{}", e);
        }
        return null;
    }

    public AccountContractResponse account(SymbolEnum symbol) {
        try {
            String response = futureAPI.futureContractAccountInfo(symbol == null ? null : symbol.getCoinName());
            return JSON.parseObject(response, AccountContractResponse.class);
        } catch (Exception e) {
            LOGGER.error("get account error. exception:{}", e);
        }
        return null;
    }

    public AccountPositionResponse position(SymbolEnum symbol) {
        try {
            String response = futureAPI.futureContractPositionInfo(symbol == null ? null : symbol.getCoinName());
            return JSON.parseObject(response, AccountPositionResponse.class);
        } catch (Exception e) {
            LOGGER.error("get position error. exception:{}", e);
        }
        return null;
    }

    public ContractOrderResponse contractOrder(Long clientOrderId, SymbolEnum symbol) {
        try {
            String response = futureAPI.futureContractOrderInfo(null, String.valueOf(clientOrderId), symbol.getCoinName(), "1");
            return JSON.parseObject(response, ContractOrderResponse.class);
        } catch (Exception e) {
            LOGGER.error("get contractOrder error. exception:{}", e);
        }
        return null;
    }

    public OrderResponse place(SymbolEnum symbol, DirectionEnum direction, OffsetEnum offset, BigDecimal price, PriceTypeEnum priceType, int lever, long volumn) {
        try {
            String response = futureAPI.futureContractOrder(
                    symbol.getCoinName(),
                    symbol.getContractType(),
                    null,
                    String.valueOf(OrderIdGenerator.generate()),
                    price == null ? "" : price.toString(),
                    String.valueOf(volumn),
                    direction.getName(),
                    offset.getName(),
                    String.valueOf(lever),
                    priceType.getName());
            return JSON.parseObject(response, OrderResponse.class);
        } catch (Exception e) {
            LOGGER.error("place order error. exception:{}", e);
        }
        return null;
    }

    public CancelOrderResponse cancelAll(String coinName) {
        try {
            String response = futureAPI.futureContractCancelall(coinName);
            return JSON.parseObject(response, CancelOrderResponse.class);
        } catch (Exception e) {
            LOGGER.error("cancel all error. exception:{}", e);
        }
        return null;
    }

}
