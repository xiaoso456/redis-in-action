package io.github.xiaoso456.demo.service;

import org.redisson.Redisson;
import org.redisson.RedissonNode;
import org.redisson.api.CronSchedule;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RScheduledExecutorService;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.RedissonNodeConfig;

import java.util.Collections;
class MyTask implements Runnable {
    @Override
    public void run() {
        System.out.println("执行任务,当前时间为" + System.currentTimeMillis());
    }
}
public class SchedulerServiceDemo {
    public static void main(String[] args) {
        Config config = new Config();


        config.useSingleServer()
                .setAddress("redis://192.168.229.128:31573")
                .setDatabase(0)
                .setPassword("123456");

        RedissonNodeConfig nodeConfig = new RedissonNodeConfig(config);
        // 设置 myExecutor 服务有 5 个workers
        nodeConfig.setExecutorServiceWorkers(Collections.singletonMap("myExecutor", 1));
        RedissonNode redissonNode = RedissonNode.create(nodeConfig);
        redissonNode.start();

        RedissonClient redissonClient = Redisson.create(config);
        RScheduledExecutorService myExecutor = redissonClient.getExecutorService("myExecutor");
        myExecutor.delete();
        myExecutor.schedule(new MyTask(), CronSchedule.of("0/5 * * * * ?"));


        // redissonClient.shutdown();

    }
}
