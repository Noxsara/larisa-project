package com.blockchain.larisa.util;

import java.util.concurrent.atomic.AtomicLong;

public class OrderIdGenerator {

    private static AtomicLong id = new AtomicLong(System.currentTimeMillis() / 1000);

    public static Long generate() {
        return id.incrementAndGet();
    }
}
