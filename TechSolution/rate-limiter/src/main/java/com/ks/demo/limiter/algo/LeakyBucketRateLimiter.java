package com.ks.demo.limiter.algo;

import java.util.concurrent.atomic.AtomicInteger;

public class LeakyBucketRateLimiter {
    private final int capacity;      // 漏斗容量（决定最大突发量）
    private final int leakRate;      // 流出速率（个/秒）
    private final long leakIntervalMs;// 流出速率（个/毫秒）

    private AtomicInteger waterLevel = new AtomicInteger(0); //漏斗初始容量
    private volatile long lastLeakTime; //上一次流出时间

    public LeakyBucketRateLimiter(int capacity, int leakRate) {
        this.capacity = capacity;
        this.leakRate = leakRate;
        this.leakIntervalMs = 1000L / leakRate;
        this.lastLeakTime = System.currentTimeMillis();
    }

    public boolean tryAcquire() {
        leak(); //每一次放入漏斗之前，先漏水

        int currentLevel = waterLevel.get();
        if (currentLevel < capacity) {
            return waterLevel.compareAndSet(currentLevel, currentLevel + 1);
        }
        return false; // 漏斗满了，拒绝
    }

    private void leak() {
        long now = System.currentTimeMillis();
        long elapsed = now - lastLeakTime;

        int leakedCount = (int) (elapsed / leakIntervalMs);

        if (leakedCount > 0) {
            int currentLevel = waterLevel.get();
            int newLevel = Math.max(0, currentLevel - leakedCount);

            if (waterLevel.compareAndSet(currentLevel, newLevel)) {
                lastLeakTime = now;
            }
        }
    }
}
