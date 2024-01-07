package io.github.xiaoso456.demo.lock;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

public class EchoTask implements Runnable{

    private String lockKey;
    private Long deleyTime;

    private String message;

    public EchoTask(String lockKey,Long delayTime,String message) {
        this.lockKey = lockKey;
        this.deleyTime = delayTime;
        this.message = message;
    }

    @Override
    public void run() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://my-redis-master.default:6379")
                .setDatabase(0)
                .setPassword("123456");
        RedissonClient redissonClient = Redisson.create(config);

        RLock lock = redissonClient.getLock(lockKey);
        lock.lock();
        System.out.println(message);
        try {
            Thread.sleep(deleyTime);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            lock.unlock();
        }


    }
}
