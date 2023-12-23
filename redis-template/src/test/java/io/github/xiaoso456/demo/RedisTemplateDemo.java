package io.github.xiaoso456.demo;

import cn.hutool.core.util.ArrayUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SpringBootTest
public class RedisTemplateDemo {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Test
    void testRestTemplate() {
        // 1.string
        redisTemplate.opsForValue().set("key1", "value1");
        String resultString = redisTemplate.opsForValue().get("key1");
        System.out.println("key1:" + resultString);

        // 2.list
        redisTemplate.delete("listKey");
        redisTemplate.opsForList().rightPush("listKey", "value1");
        redisTemplate.opsForList().rightPush("listKey", "value2");

        List<String> resultList = redisTemplate.opsForList().range("listKey", 0, -1);
        System.out.println(ArrayUtil.toString(resultList));

        // 3. set
        redisTemplate.delete("setKey");
        redisTemplate.opsForSet().add("setKey", "value1");
        redisTemplate.opsForSet().add("setKey", "value1");
        redisTemplate.opsForSet().add("setKey", "value2");

        Set<String> setResult = redisTemplate.opsForSet().members("setKey");
        System.out.println(ArrayUtil.toString(setResult));

        // 4. hash
        redisTemplate.delete("hashKey");
        redisTemplate.opsForHash().put("hashKey", "field1", "value1");
        redisTemplate.opsForHash().put("hashKey", "field2", "value2");

        Map<Object, Object> hashResult = redisTemplate.opsForHash().entries("hashKey");
        System.out.println(hashResult);

        // 5. zset
        redisTemplate.delete("zsetKey");
        redisTemplate.opsForZSet().add("zsetKey", "user1", 94.0);
        redisTemplate.opsForZSet().add("zsetKey", "user2", 98.0);
        redisTemplate.opsForZSet().add("zsetKey", "user3", 95.0);
        redisTemplate.opsForZSet().add("zsetKey", "user3", 96.0);

        // 获取排行数
        Long zsetCount = redisTemplate.opsForZSet().zCard("zsetKey");
        // 获取 user3 分数
        Double user3Score = redisTemplate.opsForZSet().score("zsetKey", "user3");
        // 获取排名
        Long user1Rank = redisTemplate.opsForZSet().rank("zsetKey", "user1");
        Long user2Rank = redisTemplate.opsForZSet().rank("zsetKey", "user2");
        Long user3Rank = redisTemplate.opsForZSet().rank("zsetKey", "user3");
        System.out.println("排行榜用户数为" + zsetCount + " user3分数为" + user3Score + " user3排名为" + user3Rank + " user2排名为" + user2Rank + " user1排名为" + user1Rank);


    }


}
