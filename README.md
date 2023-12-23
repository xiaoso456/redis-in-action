## 简介
更新一些 redis 使用示例
## lettuce
lettuce 是一个Java Redis Client库，比起Jedis不需要维护线程池就能保证了单实例线程安全

### 快速开始
示例见 LettuceDemo, 演示了如何创建一个redis连接,并调用同步客户端set和get方法


## redis-template
一般来说,不需要直接使用裸redis客户端,直接使用 spring-boot-starter-data-redis 中的 redisTemplate 即可,底层也是使用 lettuce
### 快速开始
示例见 test 下 RedisTemplateDemo,演示了使用 redisTemplate 对string、list、set、hash、zset的基本使用
### 设置刷新 redis 集群拓扑
redisTemplate 底层使用的 lettuce 客户端,建立 redis 连接后,不会去主动获取 redis 集群内其他节点的地址信息,也就是说,如果配置的redis url所在节点挂掉了,即使有其他备用节点,也无法连接


## 参考