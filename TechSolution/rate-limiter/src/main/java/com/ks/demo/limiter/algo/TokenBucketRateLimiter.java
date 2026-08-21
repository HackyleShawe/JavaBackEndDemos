package com.ks.demo.limiter.algo;

import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * 时间复杂度：O(1) —— 每次请求只做一次减法和比较
 * 空间复杂度：O(1) —— 只需 4 个变量
 */
public class TokenBucketRateLimiter {
    private final int capacity;          // 桶容量（决定最大突发量）
    private final int refillRate;        // 令牌补充速率（个/秒）
    private AtomicInteger tokens; //当前令牌桶中的含量
    private volatile long lastRefillTime; //上一次填充时间

    public TokenBucketRateLimiter(int capacity, int refillRate) {
        this.capacity = capacity;
        this.refillRate = refillRate;
        this.tokens = new AtomicInteger(capacity); // 初始满桶
        this.lastRefillTime = System.currentTimeMillis();
    }

    public boolean tryAcquire() {
        //每次从桶里获取令牌之间，都先填充一下
        refill();

        int currentTokens = tokens.get();
        while (currentTokens > 0) { //只要桶里有令牌，一直重试到获取令牌为止
            if (tokens.compareAndSet(currentTokens, currentTokens - 1)) {
                return true;
            }
            currentTokens = tokens.get();
        }
        return false;
    }

    /**
     * 惰性填充：不用定时器，每次请求时按时间差计算应填充的令牌数
     * 为什么用惰性填充而非定时器？
     * - 定时器有线程开销，百万级 Key 时不可接受
     * - 惰性填充零额外开销，且结果完全等价
     */
    private void refill() {
        long now = System.currentTimeMillis();
        long elapsed = now - lastRefillTime;

        //注意refillRate的单位是：个/s，换算为：个/ms，1000/refillRate
        int tokensToAdd = (int) (elapsed / (1000/refillRate));

        if (tokensToAdd > 0) {
            int currentTokens = tokens.get();
            int newTokens = Math.min(capacity, currentTokens + tokensToAdd);

            if (tokens.compareAndSet(currentTokens, newTokens)) {
                lastRefillTime = now;
            }
        }
    }

    /**
     * 返回需要等待的毫秒数（用于设置 Retry-After 响应头）
     * 为什么要提供这个方法？
     * - HTTP 429 规范建议返回 Retry-After，让客户端知道何时重试
     * - 避免客户端盲目重试导致"重试风暴"
     */
    public synchronized long getWaitTimeMs(int needTokens) {
        refill();
        if (tokens.get() >= needTokens) return 0;
        double deficit = needTokens - tokens.get();
        return (long) Math.ceil(deficit / refillRate * 1000);
    }
}
