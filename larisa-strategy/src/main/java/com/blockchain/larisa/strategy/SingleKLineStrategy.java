package com.blockchain.larisa.strategy;


import com.alibaba.fastjson.JSON;

import com.blockchain.larisa.common.Constants;
import com.blockchain.larisa.common.DirectionEnum;
import com.blockchain.larisa.common.KLineEnum;
import com.blockchain.larisa.common.MergeDepthEnum;
import com.blockchain.larisa.common.OffsetEnum;
import com.blockchain.larisa.common.OrderStatusEnum;
import com.blockchain.larisa.common.PriceTypeEnum;
import com.blockchain.larisa.common.SymbolEnum;
import com.blockchain.larisa.context.ProfitRate;
import com.blockchain.larisa.context.SingleKLineConfig;
import com.blockchain.larisa.context.StrategyConfig;
import com.blockchain.larisa.domain.AccountContractResponse;
import com.blockchain.larisa.domain.CancelOrderResponse;
import com.blockchain.larisa.domain.ContractOrderResponse;
import com.blockchain.larisa.domain.DepthResponse;
import com.blockchain.larisa.domain.KLine;
import com.blockchain.larisa.domain.KLineContext;
import com.blockchain.larisa.domain.KLineResponse;
import com.blockchain.larisa.domain.MailInfo;
import com.blockchain.larisa.domain.OpenContext;
import com.blockchain.larisa.domain.OrderResponse;
import com.blockchain.larisa.exception.LarisaException;
import com.blockchain.larisa.service.HuobiFutureService;
import com.blockchain.larisa.service.MailService;
import com.blockchain.larisa.util.DateUtil;
import com.blockchain.larisa.util.MailUtil;
import com.blockchain.larisa.util.ResponseUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * 15分均线策略
 * 一个策略可以跑多种合约, 因此设置为prototype
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SingleKLineStrategy extends AbstractStategy {

    private Logger LOGGER;

    private String symbolName;

    private Integer leverRate;

    private BigDecimal positionRate;

    private BigDecimal riskRate;

    private ProfitRate profitRate;

    //符号
    private SymbolEnum symbol;

    //持仓张数
    private Integer positionVol;

    //开仓价
    private BigDecimal openPrice;

    //出场价
    private BigDecimal leavePrice = BigDecimal.ZERO;

    //是否已开多
    private boolean hasBuy = false;

    //是否已开空
    private boolean hasSell = false;

    //上一次平多是否是在入场的k线上
    private boolean lastCloseBuyInOpenKline = false;

    //上一次平空是否在入场的k线上
    private boolean lastCloseSellInOpenKline = false;

    //是否手动开仓
    private volatile AtomicBoolean manualOpen = new AtomicBoolean(false);

    //是否追低或追高
    private boolean continues;

    //历史k线以及当根k线
    @Autowired
    private KLineContext kLineContext;

    @Autowired
    private HuobiFutureService huobiFutureService;

    @Autowired
    private MailService mailService;


    @Override
    public void doCreate(StrategyConfig config) {
        SingleKLineConfig singleKLineConfig = (SingleKLineConfig) config;
        symbolName = singleKLineConfig.getSymbolName();
        symbol = SymbolEnum.getByName(symbolName);

        leverRate = singleKLineConfig.getLeverRate();
        positionRate = singleKLineConfig.getPositionRate();
        riskRate = singleKLineConfig.getRiskRate();
        profitRate = singleKLineConfig.getProfitRate();

        LOGGER = LoggerFactory.getLogger(this.getClass().getSimpleName() + "_" + name());
    }

    @Override
    public void doResume() {
        //检查仓位
//        LOGGER.info("检查仓位...");
//        AccountPositionResponse accountPositionResponse = huobiFutureService.position(symbol);
//        if ()

        LOGGER.info("准备启动15m均线策略. 合约:{}, 杠杆比例:{}, 持仓比例:{}, 止损比例:{}, 收益率:{}", symbolName, leverRate, positionRate, riskRate, profitRate);

        List<KLine> kLines = prepare();
        kLineContext.init(kLines);
    }


    @Override
    public void doRun() {
        KLine kLine = refresh();
        if (kLine == null) {
            return;
        }
        kLineContext.update(kLine, hasBuy || hasSell);


        //1.判断是否卖出平多
        if (hasBuy) {
            dealBuy();
        }

        //2.判断是否买入平空
        if (hasSell) {
            dealSell();
        }

        //获取盘口
        DepthResponse depthResponse = tick();
        if (depthResponse == null) {
            return;
        }
        BigDecimal oneBuyPrice  = depthResponse.getTick().getBids().get(0).getPrice();
        BigDecimal oneSellPrice = depthResponse.getTick().getAsks().get(0).getPrice();

        //3.开多。如果已有空单, 则平掉空单
        LOGGER.info(kLineContext.getBuyStatus());
        if (!hasBuy && kLineContext.isBuy()) {
            //如果有空单, 表示反向开多, 先平掉空单
            if (hasSell) {
                LOGGER.info("当前持有卖单, 达到开多条件...");
                closeSell();
            } else {
                //平掉多单之后, 发生追高的情况
                /**
                 * a. 上次出入场是同一根k线，并且还处于上次离场k线, 那再次入场必须突破当前k线最高价.
                 * b. 上次出入场不是同一根k线, 并且还处于上次离场k线, 那再次入场必须突破当前k线最高价.
                 */
                if ((lastCloseBuyInOpenKline && kLineContext.ifCurrentInLastOpenId()) || kLineContext.ifCurrentInLastCloseId()) {
                    if (oneSellPrice.compareTo(kLineContext.getMaxHigh()) <= 0) {
                        LOGGER.info("再次准备入场, 但卖1价未突破最高价.maxHigh:{}, 卖1价:{}", kLineContext.getMaxHigh(), oneSellPrice);
                        return;
                    }
                    mailService.send(null, "【" + symbol.getName() + "】准备追高", "追高...");
                    LOGGER.info("再次突破最高价, 追高...");
                    continues = true;
                }
            }

            buy();
            return;
        }

        //4.开空。如果已有多单, 则平掉多单
        LOGGER.info(kLineContext.getSellStatus());
        if (!hasSell && kLineContext.isSell()) {
            //如果有多单, 表示反向开空, 先平掉多单
            if (hasBuy) {
                LOGGER.info("当前持有买单, 达到开空条件...");
                closeBuy();
            } else {
                //平掉空单之后, 发生追低的情况
                /**
                 * a. 上次出入场是同一根k线，并且还处于上次离场k线, 那再次入场必须突破当前k线最低价.
                 * b. 上次出入场不是同一根k线, 并且还处于上次离场k线, 那再次入场必须突破当前k线最低价.
                 */
                if ((lastCloseSellInOpenKline && kLineContext.ifCurrentInLastOpenId()) || kLineContext.ifCurrentInLastCloseId()) {
                    if (oneBuyPrice.compareTo(kLineContext.getMinLow()) >= 0) {
                        LOGGER.info("再次准备入场, 但买1价未突破最低价.minLow:{}, 买1价:{}", kLineContext.getMinLow(), oneBuyPrice);
                        return;
                    }
                    mailService.send(null, "【" + symbol.getName() + "】准备追低", "追低...");
                    LOGGER.info("再次突破最低价, 追低...");
                    continues = true;
                }
            }

            sell();
        }

    }

    //买入看多
    private void buy() {
        LOGGER.info("准备开仓做多... 历史平均收盘价:{}, 当前收盘价:{}, 历史最高价:{}, 当前最高价:{}",
                kLineContext.getHistoryCloseAverage(), kLineContext.getCurrentKline().getClose(), kLineContext.getHistoryMaxHigh(), kLineContext.getCurrentKline().getHigh());

        OpenContext openContext = openContext(leverRate);

        Integer availableVol = openContext.getTotalVol();
        if (availableVol.equals(0)) {
            return;
        }
        positionVol = BigDecimal.valueOf(availableVol).multiply(positionRate).intValue();
        if (positionVol.equals(0)) {
            return;
        }
        LOGGER.info("可开张数:{}, 实开张数:{}", availableVol, positionVol);
        //以对手价买入
        OrderResponse response = huobiFutureService.place(symbol, DirectionEnum.BUY, OffsetEnum.OPEN, null, PriceTypeEnum.OPPONENT, leverRate, positionVol);
        if (ResponseUtil.isSuccess(response)) {
            Long clientOrderId = response.getData().getClientOrderId();
            LOGGER.info("买入成功. 订单id:{}", clientOrderId);

            //获取实际成交价格
            openPrice = getOrderPrice(clientOrderId);
            if (openPrice.compareTo(BigDecimal.ZERO) == 0) {
                LOGGER.info("未成交.");
                return;
            }
            LOGGER.info("平均成交价:{}", openPrice);

            MailInfo mailInfo = MailUtil.buildOpenMail(symbol, openPrice, positionVol, leverRate, DirectionEnum.BUY);
            mailService.send(null, mailInfo.getTitle(), mailInfo.getContent());

            hasBuy = true;
            kLineContext.recordOpenKLineId();
        } else {
            LOGGER.info(JSON.toJSONString(response));
        }

    }

    //卖出看空
    private void sell() {
        LOGGER.info("准备开仓做空... 历史平均收盘价:{}, 当前收盘价:{}, 历史最低价:{}, 当前最低价:{}",
                kLineContext.getHistoryCloseAverage(), kLineContext.getCurrentKline().getClose(), kLineContext.getHistoryMinLow(), kLineContext.getCurrentKline().getLow());

        OpenContext openContext = openContext(leverRate);
        Integer availableVol = openContext.getTotalVol();
        if (availableVol.equals(0)) {
            return;
        }
        positionVol = BigDecimal.valueOf(availableVol).multiply(positionRate).intValue();
        if (positionVol.equals(0)) {
            return;
        }
        LOGGER.info("可开张数:{}, 实开张数:{}", availableVol, positionVol);
        //以对手价卖出
        OrderResponse response = huobiFutureService.place(symbol, DirectionEnum.SELL, OffsetEnum.OPEN, null, PriceTypeEnum.OPPONENT, leverRate, positionVol);
        if (ResponseUtil.isSuccess(response)) {
            Long clientOrderId = response.getData().getClientOrderId();
            LOGGER.info("卖出成功. 订单id:{}", clientOrderId);

            //获取实际成交价格
            openPrice = getOrderPrice(clientOrderId);
            if (openPrice.compareTo(BigDecimal.ZERO) == 0) {
                LOGGER.info("未成交.");
                return;
            }
            LOGGER.info("平均成交价:{}", openPrice);

            MailInfo mailInfo = MailUtil.buildOpenMail(symbol, openPrice, positionVol, leverRate, DirectionEnum.SELL);
            mailService.send(null, mailInfo.getTitle(), mailInfo.getContent());

            hasSell = true;
            kLineContext.recordOpenKLineId();

        } else {
            LOGGER.info(JSON.toJSONString(response));
        }

    }

    //处理多单
    private void dealBuy() {
        DepthResponse depthResponse = huobiFutureService.depth(symbol, MergeDepthEnum.STEP_0);
        if (ResponseUtil.isSuccess(depthResponse)) {
            BigDecimal oneBuyPrice = depthResponse.getTick().getBids().get(0).getPrice();

            int profitLevel = -2;
            BigDecimal maxHigh = kLineContext.getMaxHigh();

//            //0.如果有追单的情况，出场价设置为入场价
//            if (continues) {
//                leavePrice = openPrice;
//            } else {
//
//            }
            //1.超过highRate的盈利, 回撤highLeaveRate即止盈出场。保证大单有足够多的收益
            if (maxHigh.compareTo(openPrice.multiply(BigDecimal.ONE.add(profitRate.getHighRate2()))) >= 0) {
                leavePrice = maxHigh.multiply(BigDecimal.ONE.subtract(profitRate.getHighLeaveRate2()));
                profitLevel = 2;
            } else if (maxHigh.compareTo(openPrice.multiply(BigDecimal.ONE.add(profitRate.getHighRate1()))) >= 0) {
                leavePrice = maxHigh.multiply(BigDecimal.ONE.subtract(profitRate.getHighLeaveRate1()));
                profitLevel = 1;
            } else if (maxHigh.compareTo(openPrice.multiply(BigDecimal.ONE.add(profitRate.getHighRate0()))) >= 0) {
                leavePrice = maxHigh.multiply(BigDecimal.ONE.subtract(profitRate.getHighLeaveRate0()));
                profitLevel = 0;
            } else {
                //2.未超过highRate的盈利, 使用移动止损
                leavePrice = (openPrice.multiply(BigDecimal.valueOf(0.4))
                        .add(kLineContext.getMaxHigh().multiply(BigDecimal.valueOf(0.6))))
                        .multiply(BigDecimal.ONE.subtract(riskRate));
                profitLevel = -1;
            }


            LOGGER.info("当前买1价:{}, 历史最高价:{}, 预期出场价:{}, 预计收益级别:{}", oneBuyPrice, maxHigh, leavePrice, profitLevel);

            //3.出场
            if (leavePrice.compareTo(oneBuyPrice) >= 0) {
                closeBuy();
            }

        } else {
            LOGGER.info(JSON.toJSONString(depthResponse));
        }
    }


    private void closeBuy() {
        LOGGER.info("准备卖出平多...");
        OrderResponse orderResponse = huobiFutureService.place(symbol, DirectionEnum.SELL, OffsetEnum.CLOSE, null, PriceTypeEnum.OPPONENT, leverRate, positionVol);
        if (!ResponseUtil.isSuccess(orderResponse)) {
            LOGGER.error("卖出平多不成功, response:{}", JSON.toJSONString(orderResponse));
            return;
        }
        Long clientOrderId = orderResponse.getData().getClientOrderId();
        ContractOrderResponse contractOrderResponse = null;
        while (!ResponseUtil.isSuccess(contractOrderResponse)) {
            try {
                Thread.sleep(2000);
                contractOrderResponse = huobiFutureService.contractOrder(clientOrderId, symbol);
            } catch (Exception e) {
                LOGGER.error("get order exception, clientOrderId:{}, ex:{}", clientOrderId, e);
            }
        }
        LOGGER.info("平单后订单状态:{}", JSON.toJSONString(contractOrderResponse));

        Integer status = contractOrderResponse.getData().get(0).getStatus();
        if (!OrderStatusEnum.ALL_TRADE.getStatus().equals(status)) {
            mailService.send(null, "订单:" + clientOrderId + "未全部卖出平多, 状态:" + status, "");

            //剩余单子先撤了, 不管结果
            huobiFutureService.cancelAll(symbol.getCoinName());
            DateUtil.sleep(200);
            BigDecimal tradeVol = contractOrderResponse.getData().get(0).getTradeVolume();
            positionVol -= Objects.isNull(tradeVol) ? 0 : tradeVol.intValue();
            if (positionVol > 0) {
                //重试一次
                OrderResponse retryResponse = huobiFutureService.place(symbol, DirectionEnum.SELL, OffsetEnum.CLOSE, null, PriceTypeEnum.OPPONENT, leverRate, positionVol);
                LOGGER.info("重试结果:{}", JSON.toJSONString(retryResponse));
            }
        }

        BigDecimal sellPrice = contractOrderResponse.getData().get(0).getTradeAvgPrice();
        MailInfo mailInfo = MailUtil.buildCloseMail(symbol, openPrice, sellPrice, positionVol, leverRate, DirectionEnum.SELL);
        mailService.send(null, mailInfo.getTitle(), mailInfo.getContent());

        hasBuy = false;
        kLineContext.resetMaxHistory();
        kLineContext.recordCloseKLineId();
        continues = false;

        //出场k线是否等于入场k线
        lastCloseBuyInOpenKline = kLineContext.ifCurrentInLastOpenId() && sellPrice.compareTo(openPrice) >= 0;
    }


    //处理空单
    private void dealSell() {
        DepthResponse depthResponse = huobiFutureService.depth(symbol, MergeDepthEnum.STEP_0);
        if (ResponseUtil.isSuccess(depthResponse)) {
            BigDecimal oneSellPrice = depthResponse.getTick().getAsks().get(0).getPrice();

            int profitLevel = -2;
            BigDecimal minLow = kLineContext.getMinLow();

//            //0.如果有追单的情况，出场价设置为入场价
//            if (continues) {
//                leavePrice = openPrice;
//            } else {
//
//            }

            //1.超过highRate的盈利, 回撤highLeaveRate即止盈出场。保证大单有足够多的收益
            if (minLow.compareTo(openPrice.multiply(BigDecimal.ONE.subtract(profitRate.getHighRate2()))) <= 0) {
                leavePrice = minLow.multiply(BigDecimal.ONE.add(profitRate.getHighLeaveRate2()));
                profitLevel = 2;
            } else if (minLow.compareTo(openPrice.multiply(BigDecimal.ONE.subtract(profitRate.getHighRate1()))) <= 0) {
                leavePrice = minLow.multiply(BigDecimal.ONE.add(profitRate.getHighLeaveRate1()));
                profitLevel = 1;
            } else if (minLow.compareTo(openPrice.multiply(BigDecimal.ONE.subtract(profitRate.getHighRate0()))) <= 0) {
                leavePrice = minLow.multiply(BigDecimal.ONE.add(profitRate.getHighLeaveRate0()));
                profitLevel = 0;
            } else {
                //2.未超过highRate的盈利, 使用移动止损
                leavePrice = (openPrice.multiply(BigDecimal.valueOf(0.4))
                        .add(kLineContext.getMinLow().multiply(BigDecimal.valueOf(0.6))))
                        .multiply(BigDecimal.ONE.add(riskRate));
            }

            LOGGER.info("当前卖1价:{}, 历史最低价:{}, 预期出场价:{}, 预计收益级别:{}", oneSellPrice, minLow, leavePrice, profitLevel);

            if (leavePrice.compareTo(oneSellPrice) <= 0) {
                closeSell();
            }
        } else {
            LOGGER.info(JSON.toJSONString(depthResponse));
        }

    }

    private void closeSell() {
        LOGGER.info("准备买入平空...");
        OrderResponse orderResponse = huobiFutureService.place(symbol, DirectionEnum.BUY, OffsetEnum.CLOSE, null, PriceTypeEnum.OPPONENT, leverRate, positionVol);
        if (!ResponseUtil.isSuccess(orderResponse)) {
            LOGGER.error("买入平空不成功, response:{}", JSON.toJSONString(orderResponse));
            return;
        }
        Long clientOrderId = orderResponse.getData().getClientOrderId();
        ContractOrderResponse contractOrderResponse = null;
        while (!ResponseUtil.isSuccess(contractOrderResponse)) {
            try {
                Thread.sleep(2000);
                contractOrderResponse = huobiFutureService.contractOrder(clientOrderId, symbol);
            } catch (Exception e) {
                LOGGER.error("get order exception, clientOrderId:{}, ex:{}", clientOrderId, e);
            }
        }
        LOGGER.info("平单后订单状态:{}", JSON.toJSONString(contractOrderResponse));

        Integer status = contractOrderResponse.getData().get(0).getStatus();
        if (!OrderStatusEnum.ALL_TRADE.getStatus().equals(status)) {
            mailService.send(null, "订单:" + clientOrderId + "未全部买入平空, 状态:" + status, "");

            //剩余单子先撤了, 不管结果
            huobiFutureService.cancelAll(symbol.getCoinName());
            DateUtil.sleep(200);
            BigDecimal tradeVol = contractOrderResponse.getData().get(0).getTradeVolume();
            positionVol -= Objects.isNull(tradeVol) ? 0 : tradeVol.intValue();
            if (positionVol > 0) {
                //重试一次
                OrderResponse retryResponse = huobiFutureService.place(symbol, DirectionEnum.BUY, OffsetEnum.CLOSE, null, PriceTypeEnum.OPPONENT, leverRate, positionVol);
                LOGGER.info("重试结果:{}", JSON.toJSONString(retryResponse));
            }
        }

        BigDecimal sellPrice = contractOrderResponse.getData().get(0).getTradeAvgPrice();
        MailInfo mailInfo = MailUtil.buildCloseMail(symbol, openPrice, sellPrice, positionVol, leverRate, DirectionEnum.BUY);
        mailService.send(null, mailInfo.getTitle(), mailInfo.getContent());

        hasSell = false;
        kLineContext.resetMaxHistory();
        kLineContext.recordCloseKLineId();
        continues = false;

        //出场k线是否等于入场k线
        lastCloseSellInOpenKline = kLineContext.ifCurrentInLastOpenId() && sellPrice.compareTo(openPrice) <= 0;
    }

    /**
     * 开仓时, 查询成交价格
     * 1.全部成交流程正常流转
     * 2.部分成交先撤单, 流程正常流转
     * 3.未成交先撤单, 流程重新开始
     * 有一点问题：部分成交后返回avgPrice, 但是剩下部分也成交了, 导致实际avgPrice不太准。
     */
    private BigDecimal getOrderPrice(Long clientOrderId) {
        ContractOrderResponse contractOrderResponse = null;
        BigDecimal avgPrice = BigDecimal.ZERO;
        while (true) {
            try {
                Thread.sleep(2000); //反正要sleep一下
                contractOrderResponse = huobiFutureService.contractOrder(clientOrderId, symbol);
                if (ResponseUtil.isSuccess(contractOrderResponse)) {
                    Integer status = contractOrderResponse.getData().get(0).getStatus();
                    //1. 已撤单, 直接结束
                    if (OrderStatusEnum.ALREADY_CANCEL.getStatus().equals(status)) {
                        LOGGER.info("已撤单, 订单id:{}", clientOrderId);
                        mailService.send(null, "订单:" + clientOrderId + "已撤销.", "订单撤销.");
                        break;
                    }

                    //2.如果没有全部成交, 则进行撤单, 不关心撤单结果。
                    if (!OrderStatusEnum.ALL_TRADE.getStatus().equals(status)) {
                        LOGGER.info("订单id:{}未完全成交, 状态:{}.", clientOrderId, status);
                        CancelOrderResponse cancelOrderResponse = huobiFutureService.cancelAll(symbol.getCoinName());
                        LOGGER.info("撤单结果:{}", JSON.toJSONString(cancelOrderResponse));
                        mailService.send(null, "订单:" + clientOrderId + "未完全成交,状态:" + status, "撤单结果:\n" + JSON.toJSONString(cancelOrderResponse));
                    }

                    //3.完全成交, 则获取成交价并返回。否则一直循环, 直至全部成交或者撤单成功
                    if (OrderStatusEnum.ALL_TRADE.getStatus().equals(status) || OrderStatusEnum.PART_TRADE.getStatus().equals(status)) {
                        //真实持仓量
                        positionVol = contractOrderResponse.getData().get(0).getTradeVolume().intValue();
                        LOGGER.info("有成交, 成交数量:{}", positionVol);
                        avgPrice = contractOrderResponse.getData().get(0).getTradeAvgPrice().setScale(5, RoundingMode.HALF_UP);
                        break;
                    }
                } else {
                    LOGGER.error("获取合约成交价出错, clientOrderId:{}, response:{}", clientOrderId, JSON.toJSONString(contractOrderResponse));
                }
            } catch (Exception e) {
                LOGGER.error("获取合约成交价异常, clientOrderId:{}, response:{}, exception:{}", clientOrderId, JSON.toJSONString(contractOrderResponse), e);
            }
        }
        return avgPrice;
    }


    private List<KLine> prepare() {
        Date startTime = new Date();
        LOGGER.info("拉取历史k线...");
        List<KLine> klines = null;
        while (CollectionUtils.isEmpty(klines)) {
            KLineResponse kLineResponse = huobiFutureService.kline(symbol, KLineEnum.FIFTEEN_MINUTE, String.valueOf(Constants.LAST_FIFTY_KLINE + 1));
            if (ResponseUtil.isSuccess(kLineResponse)) {
                klines = kLineResponse.getData();
            }
        }
        Date endTime = new Date();
        LOGGER.info("历史k线拉取完成,耗时:{}ms", endTime.getTime() - startTime.getTime());
        return klines;
    }

    private KLine refresh() {
        if (DateUtil.skip()) {
            LOGGER.info("到达15分均线变更时间点...");
            return null;
        }

        KLineResponse kLineResponse = huobiFutureService.kline(symbol, KLineEnum.FIFTEEN_MINUTE, "1");
        if (ResponseUtil.isSuccess(kLineResponse)) {
            List<KLine> kLines = kLineResponse.getData();
            return kLines.get(0);
        } else {
            LOGGER.info("拉取当前k线失败, response:{}", JSON.toJSONString(kLineResponse));
        }
        return null;
    }

    private DepthResponse tick() {
        DepthResponse depthResponse = huobiFutureService.depth(symbol, MergeDepthEnum.STEP_0);
        if (!ResponseUtil.isSuccess(depthResponse)) {
            return null;
        }

        return depthResponse;
    }

    //计算开多或开空需要的一些上下文
    private OpenContext openContext(Integer leverRate) {
        LOGGER.info("获取开仓所需信息...");
        //获取单张价格, EOS/ETH固定为10, 也可以通过代码获取
//        ContractResponse contractResponse = HuobiFutureClient.contract(symbol);
//        if (!ResponseUtil.isSuccess(contractResponse)) {
//            throw new LarisaException("get contract info fail.");
//        }
//        BigDecimal contractSize = contractResponse.getData().get(0).getContractSize();
        BigDecimal contractSize = BigDecimal.TEN;

        //获取币的余额
        Future<AccountContractResponse> accountContractResponseFuture = executor.submit(new BalanceTask());
        //获取卖1价
        Future<DepthResponse> depthResponseFuture = executor.submit(new PriceTask());

        //余额
        BigDecimal balance = BigDecimal.ZERO;
        //买1价
        BigDecimal oneBuyPrice = BigDecimal.ZERO;
        //卖1价
        BigDecimal oneSellPrice = BigDecimal.ZERO;
        try {
            AccountContractResponse accountContractResponse = accountContractResponseFuture.get();
            if (!ResponseUtil.isSuccess(accountContractResponse)) {
                throw new LarisaException("get account contract fail.");
            }
            balance = accountContractResponse.getData().get(0).getMarginBalance();

            DepthResponse depthResponse = depthResponseFuture.get();
            if (!ResponseUtil.isSuccess(depthResponse)) {
                throw new LarisaException("get depth fail.");
            }
            oneBuyPrice = depthResponse.getTick().getBids().get(0).getPrice();
            oneSellPrice = depthResponse.getTick().getAsks().get(0).getPrice();
        } catch (Exception e) {
            LOGGER.info("initialize openContext exception:{}", e);
        }

        LOGGER.info("单张价值:{}", contractSize);
        LOGGER.info("账户权益:{}", balance);
        LOGGER.info("当前买1价:{}", oneBuyPrice);
        LOGGER.info("当前卖1价:{}", oneSellPrice);
        LOGGER.info("杠杆比例:{}", leverRate);

        OpenContext openContext = new OpenContext();
        openContext.setLeverRate(leverRate);
        openContext.setContractSize(contractSize);
        openContext.setBalance(balance);
        openContext.setOneBuyPrice(oneBuyPrice);
        openContext.setOneSellPrice(oneSellPrice);
        return openContext;
    }


    class BalanceTask implements Callable<AccountContractResponse> {
        @Override
        public AccountContractResponse call() {
            return huobiFutureService.account(symbol);
        }
    }

    class PriceTask implements Callable<DepthResponse> {
        @Override
        public DepthResponse call() {
            return huobiFutureService.depth(symbol, MergeDepthEnum.STEP_0);
        }
    }

    @Override
    public String name() {
        return symbolName;
    }

    @Override
    public void doClear() {
        LOGGER.info("准备清仓, 仓位情况: 多({}), 空({})", hasBuy, hasSell);
        while (hasBuy) {
            LOGGER.info("当前存在多单...");
            closeBuy();
        }
        while (hasSell) {
            LOGGER.info("当前存在空单...");
            closeSell();
        }
        LOGGER.info("清仓完毕.");
    }

}
