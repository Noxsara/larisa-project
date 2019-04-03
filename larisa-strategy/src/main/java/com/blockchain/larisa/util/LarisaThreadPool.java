package com.blockchain.larisa.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

public class LarisaThreadPool {

    private static ScheduledExecutorService executor = Executors.newScheduledThreadPool(8, new LarisaThreadFactory());

    public static ScheduledExecutorService executor() {
        return executor;
    }

    static class LarisaThreadFactory implements ThreadFactory {

        private AtomicLong id = new AtomicLong(1);

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            thread.setName("larisa-thread-" + id.getAndIncrement());

            return thread;
        }
    }
}
