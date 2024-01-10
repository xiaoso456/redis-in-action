package io.github.xiaoso456.demo.objects;

import org.redisson.Redisson;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

public class RateLimiterDemo {

    public static void printTime() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = format.format(date);
        System.out.println(dateString);
    }
    public static void main(String[] args) throws InterruptedException {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://192.168.229.128:31573")
                .setDatabase(0)
                .setPassword("123456");
        RedissonClient redissonClient = Redisson.create(config);

        RRateLimiter limiter = redissonClient.getRateLimiter("myLimiter");
        // 2s 生成 5 个令牌
        limiter.setRate(RateType.OVERALL, 5, 2, RateIntervalUnit.SECONDS);

        Thread t = new Thread(() -> {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            for(int i = 0; i < 10; i++){
                limiter.acquire(1);
                printTime();
            }
        });

        t.start();
        t.join();


        redissonClient.shutdown();
    }
}
