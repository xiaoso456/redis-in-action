package io.github.xiaoso456.demo.service.executor;

import cn.hutool.core.date.DateUtil;
import org.redisson.Redisson;
import org.redisson.RedissonNode;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RScheduledExecutorService;
import org.redisson.api.RedissonClient;
import org.redisson.api.annotation.RInject;
import org.redisson.config.Config;
import org.redisson.config.RedissonNodeConfig;

import java.util.Collections;

class EchoTask implements Runnable {

    @RInject
    private RedissonClient redissonClient;

    @Override
    public void run() {
        RAtomicLong atomicLong = redissonClient.getAtomicLong("auto-id");
        long andIncrement = atomicLong.getAndIncrement();
        String now = DateUtil.now();
        System.out.println(now + "分布式任务执行，任务执行自增id为" + andIncrement);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}


public class ExecutorServiceDemo {
    public static void main(String[] args) {
        Config config = new Config();


        config.useSingleServer()
                .setAddress("redis://127.0.0.1:6379")
                .setDatabase(0)
                .setPassword("123456");
        RedissonClient redissonClient = Redisson.create(config);

        RScheduledExecutorService myExecutor = redissonClient.getExecutorService("myExecutor0");
        for (int i = 0; i < 12; i++) {
            myExecutor.execute(new EchoTask());
        }

        redissonClient.shutdown();

    }
}
