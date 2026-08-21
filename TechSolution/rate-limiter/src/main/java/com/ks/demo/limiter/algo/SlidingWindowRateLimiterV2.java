package com.ks.demo.limiter.algo;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 让上一个窗口的请求数随着时间线性衰减，从而近似计算“最近一个完整窗口内”的真实请求量，避免固定窗口的突刺问题。
 *
 * 计算当前窗口的流量的加权公式：前一个窗口的剩余权重 + 当前窗口的计数
 * 为什么用加权而非精确记录每个请求时间戳？
 * - 精确记录需要 O(N) 内存（N=请求数），百万 QPS 下内存爆炸
 * - 加权近似误差 < 1%，工程上完全可接受
 *
 * 为什么这样设计？
 * 滑动窗口本质上是在模拟：最近1秒内的真实请求数
 * 但它没有真的记录：每个请求的时间戳
 * 而是：用“线性衰减”近似计算。
 * estimatedCount = prevCount * (1 - elapsedRatio) + currCount
 *
 * 时间复杂度：O(1)
 * 空间复杂度：O(1) —— 只需 prevCount + currCount + windowStart
 */
public class SlidingWindowRateLimiterV2  {
    /**
     * 最大请求数
     */
    private final long maxRequests;

    /**
     * 窗口大小（纳秒）
     */
    private final long windowSizeNanos;

    /**
     * 上一个窗口请求数
     */
    private volatile long prevCount;

    /**
     * 当前窗口请求数
     */
    private final AtomicLong currCount = new AtomicLong(0);

    /**
     * 当前窗口开始时间
     */
    private volatile long currWindowStart;

    public SlidingWindowRateLimiterV2(long maxRequests, Duration windowSize) {
        this.maxRequests = maxRequests;
        this.windowSizeNanos = windowSize.toNanos();

        // 使用单调时钟
        long now = System.nanoTime();

        // 对齐窗口边界
        this.currWindowStart = now - (now % windowSizeNanos);
    }

    /**
     * 尝试获取令牌
     */
    public synchronized boolean tryAcquire() {
        long now = System.nanoTime();
        advanceWindow(now);  // 推进窗口

        // 当前窗口已经过去的时间比例
        double elapsedRatio = (double) (now - currWindowStart) / windowSizeNanos;

        // 估算最近一个窗口内的请求量
        //1 - elapsedRatio：上一个窗口还有多少'影响力'
        //1-0.25=75%，因为当前窗口才过去25%，距离上一个窗口还很近，上一个窗口的影响力还很大
        double estimatedCount = prevCount * (1 - elapsedRatio) + currCount.get();

        // 使用 +1 判断，避免浮点误差
        if (estimatedCount + 1 > maxRequests) {
            return false;
        }

        currCount.incrementAndGet();

        //假设
        //限流：100 req/s
        //窗口：1 秒
        //上一个窗口prevCount = 80。说明：上一秒来了 80 个请求。当前窗口currCount = 20
        //当前窗口已过去 25%，elapsedRatio = 0.25
        //那么：estimatedCount = 80 * (1 - 0.25) + 20
        //即：
        //= 80 * 0.75 + 20
        //= 60 + 20
        //= 80
        //意味着：系统估算“最近1秒”大约有80个请求。
        return true;
    }

    /**
     * 推进窗口：计算当前窗口的前一个窗口的一系列数据
     */
    private void advanceWindow(long now) {
        long elapsed = now - currWindowStart;

        // 还在当前窗口
        if (elapsed < windowSizeNanos) {
            return;
        }

        // 跨越了多少个窗口
        long windowsPassed = elapsed / windowSizeNanos;

        // 只跨越1个窗口
        if (windowsPassed == 1) {
            prevCount = currCount.get();
        } else {
            // 跨多个窗口
            // 说明之前的统计已经失效
            prevCount = 0;
        }

        // 当前窗口清零
        currCount.set(0);

        // 推进到最新窗口
        currWindowStart += windowsPassed * windowSizeNanos;
    }
}
