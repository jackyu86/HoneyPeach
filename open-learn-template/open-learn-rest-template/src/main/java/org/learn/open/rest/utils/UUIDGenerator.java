package org.learn.open.rest.utils;



import java.util.concurrent.atomic.AtomicLong;

public class UUIDGenerator {

    private static final AtomicLong longGenerator = new AtomicLong(System.nanoTime());

    public UUIDGenerator() {
    }

    public static synchronized long nextUUID() {
        return longGenerator.incrementAndGet();
    }
}
