package com.ks.demo.limiter.algo;

import java.util.concurrent.atomic.AtomicInteger;


public class SlidingWindowRateLimiterV1 {
    /**
     * 单个槽位
     */
    private static class WindowSlot {
        /**
         * 当前槽位所属的时间片
         * 例如：slotSize = 100ms
         * 1700ms -> 17
         * 1800ms -> 18
         */
        volatile long windowTime = -1;
        /**
         * 当前时间片计数
         */
        AtomicInteger counter = new AtomicInteger(0);
    }

    /**
     * 最大请求数
     */
    private final int limit;
    /**
     * 整个窗口大小
     */
    private final long windowSizeMs;
    /**
     * 槽位数量
     */
    private final int slotCount;
    /**
     * 每个槽位跨度
     */
    private final long slotSizeMs;
    /**
     * 环形数组
     */
    private final WindowSlot[] slots;

    public SlidingWindowRateLimiterV1(int limit, long windowSizeMs, int slotCount) {
        if (limit <= 0) {
            throw new IllegalArgumentException("limit must > 0");
        }
        if (windowSizeMs <= 0) {
            throw new IllegalArgumentException("windowSizeMs must > 0");
        }
        if (slotCount <= 0) {
            throw new IllegalArgumentException("slotCount must > 0");
        }
        if (windowSizeMs < slotCount) {
            throw new IllegalArgumentException("windowSizeMs must >= slotCount");
        }

        this.limit = limit;
        this.windowSizeMs = windowSizeMs;
        this.slotCount = slotCount;
        this.slotSizeMs = windowSizeMs / slotCount;

        this.slots = new WindowSlot[slotCount];

        for (int i = 0; i < slotCount; i++) {
            slots[i] = new WindowSlot();
        }
    }

    /**
     * 尝试获取令牌
     */
    public synchronized boolean tryAcquire() {
        long now = System.currentTimeMillis();

        /**
         * 当前属于哪个时间片
         * 例如：slotSize = 100ms
         * now=1700 -> 17
         */
        long currentWindowTime = now / slotSizeMs;

        /**
         * 当前对应哪个槽位
         */
        int currentIndex =  (int) (currentWindowTime % slotCount);
        WindowSlot currentSlot = slots[currentIndex];

        /**
         * 槽位已经过期
         * 说明：当前槽位存的是旧时间片数据
         * 需要重置
         */
        if (currentSlot.windowTime != currentWindowTime) {
            currentSlot.windowTime = currentWindowTime;
            currentSlot.counter.set(0);
        }

        /**
         * 统计整个滑动窗口总请求数
         */
        int total = 0;

        for (WindowSlot slot : slots) {
            /**
             * 只统计窗口范围内的数据
             * 例如：
             * 当前时间片=20
             * slotCount=10
             * 那么有效范围：
             * [11,20]
             */
            if (currentWindowTime - slot.windowTime < slotCount) {

                total += slot.counter.get();
            }
        }

        /**
         * 超过限流阈值
         */
        if (total >= limit) {
            return false;
        }

        /**
         * 当前槽位 +1
         */
        currentSlot.counter.incrementAndGet();

        watch();
        return true;
    }

    /**
     * 查看内部状态
     */
    public synchronized void watch() {
        System.out.println("==============");
        for (int i = 0; i < slots.length; i++) {
            WindowSlot slot = slots[i];
            System.out.printf(
                    "slot=%d windowTime=%d count=%d%n",
                    i,
                    slot.windowTime,
                    slot.counter.get()
            );
        }
    }
}

