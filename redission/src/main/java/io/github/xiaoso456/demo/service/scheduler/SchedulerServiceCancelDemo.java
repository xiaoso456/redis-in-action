package io.github.xiaoso456.demo.service.scheduler;

import cn.hutool.core.date.DateUtil;
import org.redisson.Redisson;
import org.redisson.api.CronSchedule;
import org.redisson.api.RScheduledExecutorService;
import org.redisson.api.RScheduledFuture;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;


public class SchedulerServiceCancelDemo {
    public static void main(String[] args) {
        Config config = new Config();


        config.useSingleServer()
                .setAddress("redis://127.0.0.1:6379")
                .setDatabase(0)
                .setPassword("123456");



        RedissonClient redissonClient = Redisson.create(config);
        RScheduledExecutorService myExecutor = redissonClient.getExecutorService("mySchedulerExecutor");
        myExecutor.cancelTask("taskId");

        redissonClient.shutdown();

    }
}
