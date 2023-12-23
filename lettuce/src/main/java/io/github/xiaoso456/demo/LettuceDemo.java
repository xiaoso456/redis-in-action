package io.github.xiaoso456.demo;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

public class LettuceDemo {
    public static void main(String[] args) {
        RedisURI uri = RedisURI.builder()
                .redis("127.0.0.1")
                .withPort(6379)
                .withPassword("123456".toCharArray())
                .withDatabase(0)
                .build();

        RedisClient redisClient = RedisClient.create(uri);
        // 创建一个连接
        StatefulRedisConnection<String, String> connect = redisClient.connect();
        // 使用当前连接的同步api
        RedisCommands<String, String> syncCommand = connect.sync();
        // 设置key
        syncCommand.set("key1", "value1");
        String result = syncCommand.get("key1");
        System.out.println("key1:" + result);

        // 关闭连接
        connect.close();
        redisClient.shutdown();


    }
}
