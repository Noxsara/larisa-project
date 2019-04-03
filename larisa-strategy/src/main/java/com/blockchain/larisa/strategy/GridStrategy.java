package com.blockchain.larisa.strategy;

import com.blockchain.larisa.common.TradePairEnum;
import com.blockchain.larisa.common.TrendEnum;
import com.blockchain.larisa.context.GridConfig;
import com.blockchain.larisa.context.StrategyConfig;
import com.blockchain.larisa.domain.AccountIdInfo;
import com.blockchain.larisa.domain.Grid;
import com.blockchain.larisa.domain.GridContext;
import com.blockchain.larisa.domain.SpotOrderDetail;
import com.blockchain.larisa.domain.SpotSingleDepth;
import com.blockchain.larisa.huobi.spot.request.CreateOrderRequest;
import com.blockchain.larisa.service.HuobiSpotService;
import com.blockchain.larisa.service.MailService;
import com.blockchain.larisa.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GridStrategy extends AbstractStategy {

    private Logger LOGGER;

    private TradePairEnum tradePair;

    private BigDecimal minPositionRate;

    private BigDecimal maxPositionRate;

    private BigDecimal waveUpperPrice;

    private BigDecimal waveLowerPrice;

    private BigDecimal lastDealPrice;

    private int lastGridIndex;

    //初始化0, 跌-1, 涨1
    private int lastDirection = 0;

    private int area;

    private BigDecimal gridMoney;

    private AccountIdInfo accountIdInfo;

    @Autowired
    private GridContext gridContext;

    @Autowired
    private HuobiSpotService huobiSpotService;

    @Autowired
    private MailService mailService;

    @Override
    protected void doCreate(StrategyConfig config) {
        GridConfig gridConfig = (GridConfig) config;

        tradePair = TradePairEnum.getByPairName(gridConfig.getTradePair());
        minPositionRate = gridConfig.getMinPositionRate();
        maxPositionRate = gridConfig.getMaxPositionRate();

        waveLowerPrice = gridConfig.getWaveLowerPrice();
        waveUpperPrice = gridConfig.getWaveUpperPrice();

        area = gridConfig.getArea();

        gridMoney = gridConfig.getGridMoney();

        LOGGER = LoggerFactory.getLogger(this.getClass().getSimpleName() + "_" + name());

    }

    @Override
    protected void doResume() {
        LOGGER.info("准备启动网格策略, 交易对:{}, 持仓范围:{}, 网格区间:{}, 网格数:{}",
                tradePair.getPairName(), "[" + minPositionRate + "~" + maxPositionRate + "]", "[" + waveLowerPrice + "~" + waveUpperPrice + "]", area);

        //获取现货账户
        LOGGER.info("获取现货账户信息...");
        while (accountIdInfo == null || !accountIdInfo.isAvailable()) {
            accountIdInfo = huobiSpotService.account();
            LOGGER.info("现货账户信息:{}", accountIdInfo);
        }

        //构建网格
        gridContext.initGrid(waveLowerPrice, waveUpperPrice, area);
        LOGGER.info("构建网格:");
        gridContext.getGrids().forEach(grid -> LOGGER.info(grid.toString()));

        //最近成交网格设置为当前网格
        SpotSingleDepth depth = depth();
        gridContext.update(depth);
        LOGGER.info("当前网格:{}", gridContext.getLastGrid());

        //最近一次成交价格设置为启动时卖一价
        lastDealPrice = depth.getOneSellPrice();
        lastGridIndex = gridContext.getLastGrid().getIndex();
    }

    @Override
    protected void doRun() {
        SpotSingleDepth depth = depth();
        LOGGER.info("当前深度:{}, 上次成交价格:{}, 上次成交网格:{}, 上次方向:{}", depth, lastDealPrice, lastGridIndex, lastDirection);

        if (depth.getOneSellPrice().compareTo(lastDealPrice) <= 0) {
            Grid grid = gridContext.getGridByDepth(depth, TrendEnum.FALL);
            if (grid == null) {
                LOGGER.info("网格跌穿...");
                return;
            }
            if (grid.getIndex() < lastGridIndex) {
                LOGGER.info("跌破一个网格, gridIndex:{}, lastGridIndex:{}, lastDirection:{}", grid.getIndex(), lastGridIndex, lastDirection);
                if (lastDirection != 1) {
                    BigDecimal buyMoney = gridMoney.multiply(BigDecimal.valueOf(lastGridIndex - grid.getIndex()));
                    Long orderId = huobiSpotService.place(accountIdInfo.getId(), tradePair, CreateOrderRequest.OrderType.BUY_MARKET, buyMoney.toString());
                    LOGGER.info("买, gridIndex:{}, lastGridIndex:{}, amount:{}", grid.getIndex(), lastGridIndex, buyMoney);

                    DateUtil.sleep(1000);
                    SpotOrderDetail orderDetail = huobiSpotService.orderInfo(orderId);
                    LOGGER.info("订单id:{}, 订单详情:{}", orderId, orderDetail);

                }
                lastGridIndex = grid.getIndex();
                lastDealPrice = depth.getOneSellPrice();
                lastDirection = -1;

            }

            return;
        }

        if (depth.getOneBuyPrice().compareTo(lastDealPrice) >= 0) {
            Grid grid = gridContext.getGridByDepth(depth, TrendEnum.RISE);
            if (grid == null) {
                LOGGER.info("网格涨穿...");
                return;
            }
            if (grid.getIndex() > lastGridIndex) {
                LOGGER.info("涨过一个网格, gridIndex:{}, lastGridIndex:{}, lastDirection:{}", grid.getIndex(), lastGridIndex, lastDirection);
                if (lastDirection != -1) {
                    BigDecimal sellMoney = gridMoney.multiply(BigDecimal.valueOf(grid.getIndex() - lastGridIndex));
                    Long orderId = huobiSpotService.place(accountIdInfo.getId(), tradePair, CreateOrderRequest.OrderType.SELL_MARKET, sellMoney.divide(depth.getOneBuyPrice(), 4, RoundingMode.HALF_UP).toString());
                    LOGGER.info("卖, gridIndex:{}, lastGridIndex:{}, amount:{}", grid.getIndex(), lastGridIndex, sellMoney.divide(depth.getOneBuyPrice(), 4, RoundingMode.HALF_UP));

                    DateUtil.sleep(1000);
                    SpotOrderDetail orderDetail = huobiSpotService.orderInfo(orderId);
                    LOGGER.info("订单id:{}, 订单详情:{}", orderId, orderDetail);

                }
                lastGridIndex = grid.getIndex();
                lastDealPrice = depth.getOneBuyPrice();
                lastDirection = 1;
            }
            return;
        }

        LOGGER.info("到这里来了...");

    }

    private SpotSingleDepth depth() {
        SpotSingleDepth depth = null;
        while (depth == null) {
            depth = huobiSpotService.depth(tradePair);
        }
        return depth;
    }

    @Override
    protected void doClear() {

    }

    @Override
    public String name() {
        return tradePair.getPairName().toUpperCase();
    }
}
