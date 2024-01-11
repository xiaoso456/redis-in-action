package io.github.xiaoso456.demo.objects;

import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;

class User {
    private String id;
    private String name;
    public User() {
    }

    public User(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}

public class BucketDemo {
    public static void main(String[] args) {
        Config config = new Config();
        config.setCodec(new JsonJacksonCodec());
        config.useSingleServer()
                .setAddress("redis://192.168.229.128:31573")
                .setDatabase(0)
                .setPassword("123456");
        RedissonClient redissonClient = Redisson.create(config);

        RBucket<User> userBucket = redissonClient.getBucket("user");
        User user = userBucket.get();
        if(user == null){
            user = new User("1", "xiaoso123");
        }
        userBucket.set(user);

        System.out.println("user:" + redissonClient.getBucket("user").get());



        redissonClient.shutdown();
    }
}
