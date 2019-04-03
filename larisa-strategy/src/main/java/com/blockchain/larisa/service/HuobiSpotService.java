package com.blockchain.larisa.service;

import com.alibaba.fastjson.JSON;
import com.blockchain.larisa.common.MergeDepthEnum;
import com.blockchain.larisa.common.TradePairEnum;
import com.blockchain.larisa.common.TrendEnum;
import com.blockchain.larisa.domain.AccountIdInfo;
import com.blockchain.larisa.domain.SpotBalance;
import com.blockchain.larisa.domain.SpotOrderDetail;
import com.blockchain.larisa.domain.SpotSingleDepth;
import com.blockchain.larisa.huobi.spot.api.ApiClient;
import com.blockchain.larisa.huobi.spot.request.CreateOrderRequest;
import com.blockchain.larisa.huobi.spot.request.DepthRequest;
import com.blockchain.larisa.huobi.spot.response.Accounts;
import com.blockchain.larisa.huobi.spot.response.AccountsResponse;
import com.blockchain.larisa.huobi.spot.response.Balance;
import com.blockchain.larisa.huobi.spot.response.BalanceResponse;
import com.blockchain.larisa.huobi.spot.response.Depth;
import com.blockchain.larisa.huobi.spot.response.DepthResponse;
import com.blockchain.larisa.huobi.spot.response.OrdersDetailResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HuobiSpotService implements InitializingBean {

    public static final Logger LOGGER = LoggerFactory.getLogger(HuobiSpotService.class);

    @Value("${spot.access.key}")
    private String accessKey;

    @Value("${spot.secret.key}")
    private String secretKey;

    @Value("${huobi.spot.url}")
    private String url;

    private ApiClient client;

    @Override
    public void afterPropertiesSet() {
        client = new ApiClient(accessKey, secretKey);

    }

    /**
     * 获取现货账户信息
     * @return
     */
    public AccountIdInfo account() {
        try {
            AccountsResponse<List<Accounts>> accounts = client.accounts();
            List<Accounts> list = accounts.checkAndReturn();
            Accounts info = list.stream()
                    .filter(account -> "spot".equals(account.getType()))
                    .findFirst()
                    .orElse(null);
            if (info != null) {
                AccountIdInfo accountIdInfo = new AccountIdInfo();
                accountIdInfo.setId(info.getId());
                accountIdInfo.setType(info.getType());
                accountIdInfo.setStatus(info.getState());

                return accountIdInfo;
            }
        } catch (Exception e) {
            LOGGER.error("account exception:{}", e);
        }
        return null;
    }

    /**
     * 获取某个币种余额
     *
     * @param tradePair
     * @return
     */
    @SuppressWarnings("unchecked")
    public SpotBalance blance(TradePairEnum tradePair, Integer accountId) {
        try {
            BalanceResponse<Balance<List<LinkedHashMap<String, String>>>> balanceResponse = client.balance(String.valueOf(accountId));

            Balance<List<LinkedHashMap<String, String>>> balance = balanceResponse.getData();
            List<LinkedHashMap<String, String>> balanceList = balance.getList()
                    .stream()
                    .filter(e -> e.get("currency").equals(tradePair.getCoinName()))
                    .collect(Collectors.toList());

            SpotBalance spotBalance = new SpotBalance();
            spotBalance.setBlance(new BigDecimal(balanceList.get(0).get("balance")));
            spotBalance.setFrozen(new BigDecimal(balanceList.get(1).get("balance")));
            return spotBalance;
        } catch (Exception e) {
            LOGGER.error("blance exception:{}", e);
        }

        return null;
    }

    /**
     * 获取某个币种余额
     *
     * @param tradePair
     * @return
     */
    @SuppressWarnings("unchecked")
    public SpotBalance blance(TradePairEnum tradePair) {
        try {
            AccountIdInfo accountIdInfo = account();
            return blance(tradePair, accountIdInfo.getId());
        } catch (Exception e) {
            LOGGER.error("blance exception:{}", e);
        }
        return null;
    }

    /**
     * 获取买一卖一价
     *
     * @param tradePair
     * @return
     */
    public SpotSingleDepth depth(TradePairEnum tradePair) {
        DepthRequest request = new DepthRequest();
        request.setSymbol(tradePair.getPairName());
        request.setType(MergeDepthEnum.STEP_0.getName());

        DepthResponse depthResponse = client.depth(request);
        if (depthResponse == null || !"ok".equals(depthResponse.getStatus())) {
            LOGGER.error("depth error:{} resp:{}", depthResponse);
            return null;
        }

        Depth tick = depthResponse.getTick();
        BigDecimal oneBuyPrice = tick.getBids().get(0).get(0);
        BigDecimal oneSellPrice = tick.getAsks().get(0).get(0);

        SpotSingleDepth spotSingleDepth = new SpotSingleDepth();
        spotSingleDepth.setOneBuyPrice(oneBuyPrice.setScale(4, RoundingMode.HALF_UP));
        spotSingleDepth.setOneSellPrice(oneSellPrice.setScale(4, RoundingMode.HALF_UP));
        return spotSingleDepth;
    }


    public Long place(Integer accountId, TradePairEnum tradePair, String type, String amount) {
        try {
            CreateOrderRequest request = new CreateOrderRequest();
            request.setAccountId(String.valueOf(accountId));
            request.setSymbol(tradePair.getPairName());
            request.setType(type);
            request.setAmount(amount);
            request.setSource("api");
            return client.createOrder(request);
        } catch (Exception e) {
            LOGGER.error("place order error:{}", e);
        }
        return -1L;
    }

    @SuppressWarnings("unchecked")
    public SpotOrderDetail orderInfo(Long orderId) {
        try {
            OrdersDetailResponse<LinkedHashMap<String, String> > response = client.ordersDetail(String.valueOf(orderId));
            if (response != null && "ok".equals(response.getStatus())) {
                LinkedHashMap<String, String> orderInfo = response.getData();

                SpotOrderDetail detail = new SpotOrderDetail();
                detail.setOrderAmount(new BigDecimal(orderInfo.get("amount")));
                detail.setPrice(new BigDecimal(orderInfo.get("price")));
                detail.setType(orderInfo.get("type"));
                detail.setDealCash(new BigDecimal(orderInfo.get("field-cash-amount")));
                detail.setDealAmount(new BigDecimal(orderInfo.get("field-amount")));
                detail.setState(orderInfo.get("state"));

                return detail;
            }
        } catch (Exception e) {
            LOGGER.error("query order info error:{}, orderId:{}", e, orderId);
        }

        return null;
    }

}
