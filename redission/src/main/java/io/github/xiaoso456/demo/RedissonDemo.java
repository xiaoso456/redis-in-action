package io.github.xiaoso456.demo;

import org.redisson.config.Config;

public class RedissonDemo {
    public static void main(String[] args) {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://");
    }
}
