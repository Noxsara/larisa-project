package com.blockchain.larisa.domain;

import com.blockchain.larisa.common.TrendEnum;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.IntStream;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GridContext {

    private List<Grid> grids;

    public void initGrid(BigDecimal waveLowerPrice, BigDecimal waveUpperPrice, int area) {
        grids = Lists.newArrayListWithCapacity(area);
        BigDecimal gridDeltaPrice = (waveUpperPrice.subtract(waveLowerPrice))
                .divide(BigDecimal.valueOf(area), 4, RoundingMode.HALF_UP);

        Grid firstGrid = new Grid();
        firstGrid.setIndex(0);
        firstGrid.setLowerPrice(waveLowerPrice);
        firstGrid.setUpperPrice(waveLowerPrice.add(gridDeltaPrice));
        grids.add(firstGrid);

        IntStream.range(1, area).forEach(index -> {
            Grid pre = grids.get(index - 1);

            Grid current = new Grid();
            current.setIndex(index);
            current.setLowerPrice(pre.getUpperPrice());
            current.setUpperPrice(pre.getUpperPrice().add(gridDeltaPrice));
            grids.add(current);
        });

    }

    public Grid getGridByDepth(SpotSingleDepth depth, TrendEnum trendEnum) {
        BigDecimal price = trendEnum == TrendEnum.FALL ? depth.getOneSellPrice() : depth.getOneBuyPrice();
        return grids.stream()
                .filter(g -> g.getLowerPrice().compareTo(price) <=0 && g.getUpperPrice().compareTo(price) >0)
                .findFirst()
                .orElse(null);
    }

    public List<Grid> getGrids() {
        return grids;
    }

    public void setGrids(List<Grid> grids) {
        this.grids = grids;
    }

    @Override
    public String toString() {
        return "GridContext{" +
                "grids=" + grids +
                '}';
    }
}
