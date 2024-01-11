package io.github.xiaoso456.demo.objects;

import org.redisson.Redisson;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

public class BloomFilterDemo {
    public static void main(String[] args) {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://192.168.229.128:31573")
                .setDatabase(0)
                .setPassword("123456");
        RedissonClient redissonClient = Redisson.create(config);
        RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter("bloomFilter");
        bloomFilter.delete();
        // 预期插入量为100_000_000L,误报率为3%
        bloomFilter.tryInit(100_000_000, 0.03);

        bloomFilter.add("a");
        bloomFilter.add("b");
        bloomFilter.add("c");
        bloomFilter.add("d");


        boolean contain = bloomFilter.contains("a");
        System.out.println("布隆过滤器是否包含a：" + contain);

        long count = bloomFilter.count();
        System.out.println("布隆过滤器已存储预估元素数量：" + count);

        redissonClient.shutdown();

    }
}
