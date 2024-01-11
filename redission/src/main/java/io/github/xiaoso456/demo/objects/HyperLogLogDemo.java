package io.github.xiaoso456.demo.objects;

import org.redisson.Redisson;
import org.redisson.api.RHyperLogLog;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.Arrays;

public class HyperLogLogDemo {
    public static void main(String[] args) {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://192.168.229.128:31573")
                .setDatabase(0)
                .setPassword("123456");
        RedissonClient redissonClient = Redisson.create(config);
        RHyperLogLog<String> hyperLogLog0 = redissonClient.getHyperLogLog("hyperLogLog0");
        hyperLogLog0.delete();
        hyperLogLog0.add("1");
        hyperLogLog0.add("2");
        hyperLogLog0.add("3");
        hyperLogLog0.addAll(Arrays.asList("10", "20", "30"));
        System.out.println("hyperLogLog0 count:" + hyperLogLog0.count());

        RHyperLogLog<String> hyperLogLog1 = redissonClient.getHyperLogLog("hyperLogLog1");
        hyperLogLog1.delete();
        hyperLogLog1.add("4");
        hyperLogLog1.add("5");
        hyperLogLog1.add("6");

        hyperLogLog1.add("4");
        hyperLogLog1.add("5");
        hyperLogLog1.add("6");
        RHyperLogLog<String> hyperLogLog2 = redissonClient.getHyperLogLog("hyperLogLog2");
        hyperLogLog2.delete();


        System.out.println("hyperLogLog2 count with hyperLogLog1:" + hyperLogLog2.countWith(hyperLogLog1.getName()));
        hyperLogLog2.mergeWith(hyperLogLog1.getName());

        System.out.println("hyperLogLog2 count:" + hyperLogLog2.count());



        redissonClient.shutdown();
    }
}
