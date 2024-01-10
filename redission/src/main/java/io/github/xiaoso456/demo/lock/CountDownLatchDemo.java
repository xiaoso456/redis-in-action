package io.github.xiaoso456.demo.lock;

import org.redisson.Redisson;
import org.redisson.api.RCountDownLatch;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CountDownLatchDemo {
    public static void main(String[] args) throws InterruptedException {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://192.168.229.128:31573")
                .setDatabase(0)
                .setPassword("123456");
        RedissonClient redissonClient = Redisson.create(config);


        ExecutorService executor = Executors.newFixedThreadPool(2);

        final RCountDownLatch latch = redissonClient.getCountDownLatch("latch1");
        latch.trySetCount(1);

        executor.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                latch.countDown();
                System.out.println("countDown,current count:" + latch.getCount());
            }

        });

        executor.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    latch.await(5000, TimeUnit.MILLISECONDS);
                    System.out.println("await finished,current count:" + latch.getCount());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        });


        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        redissonClient.shutdown();


    }



}
