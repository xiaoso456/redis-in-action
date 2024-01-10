package io.github.xiaoso456.demo.lock;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.concurrent.TimeUnit;

public class LockDemo {
    public static void main(String[] args) throws InterruptedException {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://192.168.229.128:31573")
                .setDatabase(0)
                .setPassword("123456");
        RedissonClient redissonClient = Redisson.create(config);

        RLock lock1 = redissonClient.getLock("lock");
        // 获取锁后最多2s后释放
        lock1.lock(2, TimeUnit.SECONDS);

        Thread t = new Thread() {
            public void run() {
                System.out.println(System.currentTimeMillis());
                RLock lock2 = redissonClient.getLock("lock");
                // 调用 lock 约2s后才能获得锁
                lock2.lock();
                System.out.println(System.currentTimeMillis());
                lock2.unlock();
            };
        };

        t.start();
        t.join();

        redissonClient.shutdown();



    }
}
