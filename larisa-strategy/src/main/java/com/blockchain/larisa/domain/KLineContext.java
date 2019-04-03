package com.blockchain.larisa.domain;

import com.blockchain.larisa.common.Constants;
import com.blockchain.larisa.exception.LarisaException;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

/**
 * K线上下文. 一个策略一个上下文, 因此设置为prototype
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class KLineContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(KLineContext.class);

    //当根k线
    private KLine currentKline = null;

    //最近50根k线，用来判断是否做多或者做空
    private List<KLine> historyKLines;

    //做空或者做多以后的300根k线，用来判断是否离场
    private List<KLine> maxHistoryKLines;

    //上次开仓K线id
    private Long lastOpenKLineId = 0L;

    //上次平仓K线id
    private Long lastCloseKlineId = 0L;

    public void init(List<KLine> kLines) {
        if (kLines.size() != (Constants.LAST_FIFTY_KLINE + 1)) {
            throw new LarisaException("initial kline num error");
        }

        historyKLines = Lists.newArrayListWithCapacity(Constants.LAST_FIFTY_KLINE);
        maxHistoryKLines = Lists.newArrayList();

        //当前k线
        int len = kLines.size();
        currentKline = kLines.get(len - 1);

        //最近50根k线
        kLines.remove(len - 1);
        historyKLines.addAll(kLines);
    }

    /**
     * 添加最新的15m线到历史记录里面
     *
     * @param newKLine 最新的15m线
     * @param hasOpen 是否已开仓
     */
    public void update(KLine newKLine, boolean hasOpen) {
        if (Objects.isNull(newKLine)) {
            throw new LarisaException("kline is null");
        }

        //当前k线没有变更
        if (newKLine.getId().equals(currentKline.getId())) {
            currentKline = newKLine;
            return;
        }

        //当前k线发生变更
        LOGGER.info("k线发生变更. 老k线:{}, 新k线:{}", currentKline.getId(), newKLine.getId());
        historyKLines.remove(0);
        historyKLines.add(currentKline);

        //已经开仓的情况下, 更新用于出场的k线信息
        //主要目的是用于计算开仓以来的最高价或最低价, 并用于计算出场价格
        if (hasOpen) {
            maxHistoryKLines.add(currentKline);
            if (maxHistoryKLines.size() > Constants.MAX_KLINE_HISTORY) {
                maxHistoryKLines.remove(0);
            }
            LOGGER.info("开仓后历史k线记录数:{}", maxHistoryKLines.size());
        }

        currentKline = newKLine;
    }

    /**
     * 清空开仓以来的历史k线信息
     */
    public void resetMaxHistory() {
        maxHistoryKLines.clear();
    }

    /**
     * 判断是否买入多单
     *
     * @return void
     */
    public boolean isBuy() {
        return currentKline.getClose().compareTo(getHistoryCloseAverage()) >= 0
                && currentKline.getHigh().compareTo(getHistoryMaxHigh()) >= 0;
    }

    public String getBuyStatus() {
        return String.format("当前收盘:%s, 历史平均收盘:%s, 当前最高:%s, 历史最高:%s, 历史最高收盘:%s, 最高平均:%s",
                currentKline.getClose(), getHistoryCloseAverage(), currentKline.getHigh(), getHistoryMaxHigh(), getHistoryMaxClose(), (getHistoryMaxClose().add(getHistoryMaxHigh())).divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP));
    }

    /**
     * 判断是否开空
     *
     * @return void
     */
    public boolean isSell() {
        return currentKline.getClose().compareTo(getHistoryCloseAverage()) <= 0
                && currentKline.getLow().compareTo(getHistoryMinLow()) <= 0;
    }

    public String getSellStatus() {
        return String.format("当前收盘:%s, 历史平均收盘:%s, 当前最低:%s, 历史最低:%s, 历史最低收盘价:%s, 最低平均值:%s",
                currentKline.getClose(), getHistoryCloseAverage(), currentKline.getLow(), getHistoryMinLow(), getHistoryMinClose(), (getHistoryMinClose().add(getHistoryMinLow())).divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP));
    }



    /**
     * 计算过去50根15m线的收盘价平均值
     *
     * @return 均值
     */
    public BigDecimal getHistoryCloseAverage() {
        Double historyCloseAverage = historyKLines.stream()
                .mapToDouble(kline -> kline.getClose().doubleValue())
                .average()
                .orElse(0.0);
        return BigDecimal.valueOf(historyCloseAverage);
    }

    /**
     * 计算过去30根15m线的最高收盘价
     */
    public BigDecimal getHistoryMaxClose() {
        Double maxClose = historyKLines.stream()
                .mapToDouble(kline -> kline.getClose().doubleValue())
                .skip(Constants.LAST_FIFTY_KLINE - Constants.LAST_THIRTY_KLINE)
                .max()
                .orElse(0.0);
        return BigDecimal.valueOf(maxClose);
    }

    /**
     * 计算过去30根15m线的最高价, 不包括当前k线
     *
     * @return 历史最高值
     */
    public BigDecimal getHistoryMaxHigh() {
        Double maxHigh = historyKLines.stream()
                .mapToDouble(kline -> kline.getHigh().doubleValue())
                .skip(Constants.LAST_FIFTY_KLINE - Constants.LAST_THIRTY_KLINE)
                .max()
                .orElse(0.0);
        return BigDecimal.valueOf(maxHigh);
    }

    /**
     * 计算过去30根15m线的最低收盘价
     */
    public BigDecimal getHistoryMinClose() {
        Double minClose = historyKLines.stream()
                .mapToDouble(kline -> kline.getClose().doubleValue())
                .skip(Constants.LAST_FIFTY_KLINE - Constants.LAST_THIRTY_KLINE)
                .min()
                .orElse(0.0);
        return BigDecimal.valueOf(minClose);
    }


    /**
     * 计算过去30根15m线的最低价, 不包括当前k线
     *
     * @return 最低值
     */
    public BigDecimal getHistoryMinLow() {
        Double minLow = historyKLines.stream()
                .mapToDouble(kline -> kline.getLow().doubleValue())
                .skip(Constants.LAST_FIFTY_KLINE - Constants.LAST_THIRTY_KLINE)
                .min()
                .orElse(0.0);
        return BigDecimal.valueOf(minLow);
    }

    /**
     * 计算开多以后出现的最高价, 包括当前k线
     *
     * @return 至今最高价
     */
    public BigDecimal getMaxHigh() {
        List<KLine> temp = Lists.newArrayList(maxHistoryKLines);
        temp.add(currentKline);
        Double maxHigh = temp.stream()
                .mapToDouble(kLine -> kLine.getHigh().doubleValue())
                .max()
                .orElse(0.0);
        return BigDecimal.valueOf(maxHigh);
    }

    /**
     * 计算开空以后出现的最低价, 包括当前k线
     *
     * @return 至今最低价
     */
    public BigDecimal getMinLow() {
        List<KLine> temp = Lists.newArrayList(maxHistoryKLines);
        temp.add(currentKline);
        Double minLow = temp.stream()
                .mapToDouble(kLine -> kLine.getLow().doubleValue())
                .min()
                .orElse(0.0);
        return BigDecimal.valueOf(minLow);
    }

    public KLine getCurrentKline() {
        return this.currentKline;
    }

    /**
     * 记录上次开仓k线id
     */
    public void recordOpenKLineId() {
        this.lastOpenKLineId = currentKline.getId();
    }


    /**
     * 当前k线是否处于上次开仓id
     */
    public boolean ifCurrentInLastOpenId() {
        return lastOpenKLineId.equals(currentKline.getId());
    }

    /**
     * 记录上次平仓k线id
     */
    public void recordCloseKLineId() {
        this.lastCloseKlineId = currentKline.getId();
    }

    /**
     * 当前k线是否处于上次平仓id
     */
    public boolean ifCurrentInLastCloseId() {
        return lastCloseKlineId.equals(currentKline.getId());
    }



}
