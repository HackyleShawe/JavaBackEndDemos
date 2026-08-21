package com.ks.demo.limiter.algo;


/**
 * 实现思路：
 * 时间窗口大小：windowSizeMs
 * 计数器：AtomicInteger
 */
public class FixedWindowRateLimiter {
    private final int limit; // 窗口内最大请求数
    private final long windowSizeMs; // 窗口大小（毫秒）

    private int count; //当前窗口的计数
    private long windowStart; //窗口开始时间

    public FixedWindowRateLimiter(int limit, long windowSizeMs) {
        this.limit = limit;
        this.windowSizeMs = windowSizeMs;

        long now = System.currentTimeMillis();
        this.windowStart = now - (now % windowSizeMs);
    }

    /**
     * 整个加锁，保证窗口的计数、比较是并发安全的
     */
    public synchronized boolean tryAcquire() {
        long now = System.currentTimeMillis();
        long currentWindow = now - (now % windowSizeMs);

        // 窗口过期，开启新窗口
        if (currentWindow != windowStart) {
            windowStart = currentWindow;
            count = 0;
        }

        count++;

        boolean ac = count <= limit;
        System.out.println("tryAcquire-windowStart=" + windowStart
                + " windowSizeMs=" + windowSizeMs
                + " limit=" + limit + " count=" + count + " ac=" + ac);

        return ac;
    }
}

