package io.github.xiaoso456.demo.service.scheduler;

import org.redisson.Redisson;
import org.redisson.RedissonNode;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.RedissonNodeConfig;

import java.util.Collections;


public class RedissionNodeDemo {
    public static void main(String[] args) {
        Config config = new Config();


        config.useSingleServer()
                .setAddress("redis://127.0.0.1:6379")
                .setDatabase(0)
                .setPassword("123456");
        RedissonClient redissonClient = Redisson.create(config);


        RedissonNodeConfig nodeConfig = new RedissonNodeConfig(config);
        // 设置 myExecutor 服务有 1 个workers
        nodeConfig.setExecutorServiceWorkers(Collections.singletonMap("mySchedulerExecutor", 1));
        RedissonNode redissonNode = RedissonNode.create(nodeConfig);
        // 当前Java进程启动作为一个RedissionNode
        redissonNode.start();


        // redissonClient.shutdown();

    }
}
