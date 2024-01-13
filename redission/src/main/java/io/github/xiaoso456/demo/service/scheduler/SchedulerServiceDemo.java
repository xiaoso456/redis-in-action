package io.github.xiaoso456.demo.service.scheduler;

import cn.hutool.core.date.DateUtil;
import org.redisson.Redisson;
import org.redisson.RedissonNode;
import org.redisson.api.CronSchedule;
import org.redisson.api.RScheduledExecutorService;
import org.redisson.api.RScheduledFuture;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.RedissonNodeConfig;

import java.util.Collections;
class MyTask implements Runnable {
    @Override
    public void run() {
        System.out.println("执行任务,当前时间为" + DateUtil.now());
    }
}
public class SchedulerServiceDemo {
    public static void main(String[] args) {
        Config config = new Config();


        config.useSingleServer()
                .setAddress("redis://127.0.0.1:6379")
                .setDatabase(0)
                .setPassword("123456");



        RedissonClient redissonClient = Redisson.create(config);
        RScheduledExecutorService myExecutor = redissonClient.getExecutorService("mySchedulerExecutor");
        RScheduledFuture<?> schedule = myExecutor.schedule("taskId",new MyTask(), CronSchedule.of("0/5 * * * * ?"));


        redissonClient.shutdown();

    }
}
