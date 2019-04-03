package com.blockchain.larisa.strategy;

import com.blockchain.larisa.common.Constants;
import com.blockchain.larisa.context.StrategyConfig;
import com.blockchain.larisa.util.LarisaThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractStategy implements Strategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractStategy.class);

    //调度器
    ScheduledExecutorService executor = LarisaThreadPool.executor();

    //策略是否已停止
    private volatile boolean stopped = false;

    //可重入锁, 保护stopped字段
    private Lock lock = new ReentrantLock();

    //清仓同步器
    private CountDownLatch latch;

    @Override
    public ScheduledFuture start(StrategyConfig config) {
        doCreate(config);
        return resume();
    }

    @Override
    public ScheduledFuture resume() {
        latch = new CountDownLatch(1);
        stopped = false;
        doResume();
        return executor.scheduleWithFixedDelay(() -> run(), 0, 100, TimeUnit.MILLISECONDS);
    }

    @Override
    public void run() {
        if (checkIfStop()) {
            return;
        }
        try {
            doRun();
        } catch (Exception e) {
            LOGGER.info("run exception:{}", e);
        }
    }

    private boolean checkIfStop() {
        try {
            lock.lock();
            if (stopped) {
                LOGGER.info("###########");
                latch.countDown();
                return true;
            }
        } catch (Exception e) {
            LOGGER.error("latch count down error:{}", e);
        } finally {
            lock.unlock();
        }
        return false;
    }

    @Override
    public String stop(ScheduledFuture scheduledFuture) {
        //1.标记策略已停止
        try {
            lock.lock();
            stopped = true;
            LOGGER.info("停止标记设置为true...");
        } catch (Exception e) {
            LOGGER.error("stop error:{}", e);
            return Constants.FALSE;
        } finally {
            lock.unlock();
        }

        //2.等待策略最后一次执行完成
        try {
            LOGGER.info("等待任务最后一次执行完成.");
            latch.await();
        } catch (Exception e) {
            LOGGER.info("latch await error:{}", e);
            return Constants.FALSE;
        }

        //3.清仓
        LOGGER.info("最后一次任务执行完成. 准备清仓");
        doClear();

        //4.取消调度
        scheduledFuture.cancel(false);
        LOGGER.info("策略已停止.");
        return Constants.TRUE;
    }

    /**
     * 创建一个策略, 上下文初始化
     * @param config 策略上下文
     */
    protected abstract void doCreate(StrategyConfig config);

    /**
     * 恢复上下文。策略启动或重新启动时, 必须要做的事情
     */
    protected abstract void doResume();

    /**
     * 策略执行逻辑
     */
    protected abstract void doRun();

    /**
     * 策略清仓
     */
    protected abstract void doClear();

}
