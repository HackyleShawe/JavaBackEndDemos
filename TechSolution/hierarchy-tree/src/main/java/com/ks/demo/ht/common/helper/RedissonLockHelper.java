package com.ks.demo.ht.common.helper;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Component
public class RedissonLockHelper {

    @Autowired
    private RedissonClient redissonClient;
    /**
     * 加锁执行业务方法，并返回值
     * @param lockKey 锁名
     * @param waitTime 获取锁的等待时间
     * @param bizLogic 锁要执行的业务逻辑
     */
    public <T> T executeWithLock(String lockKey, long waitTime, Supplier<T> bizLogic) throws RuntimeException {
        RLock lock = redissonClient.getLock(lockKey);
        boolean tryLock;
        try {
            // 尝试获取锁，等待waitTime秒后还没获取到，获取失败
            tryLock = lock.tryLock(waitTime, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // 恢复中断状态
            throw new RuntimeException("获取锁" + lockKey + "被中断!");
        }

        // 没拿到锁的处理
        if (!tryLock) {
            //log.error("锁" + lockKey + "被占用，请稍后再试");
            throw new RuntimeException("正在处理中，请勿重复操作");
        }

        // 执行业务逻辑并确保锁释放
        try {
            return bizLogic.get(); // 调用业务逻辑
        } finally {
            // 只有当前线程持有锁时才释放
            //不用这样：tryLock && lock.isHeldByCurrentThread()，因为上文已经判断过了
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
