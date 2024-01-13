## 简介
更新一些 redis 使用示例

## 环境准备

### k8s

k8s/k3s 下可以用helm快速使用现有charts创建standalone、sentinel、cluster模式的集群

helm charts参考 [redis 18.6.1 · bitnami/bitnami (artifacthub.io)](https://artifacthub.io/packages/helm/bitnami/redis)

cluster 模式 helm charts 参考 [redis-cluster 9.1.4 · bitnami/bitnami (artifacthub.io)](https://artifacthub.io/packages/helm/bitnami/redis-cluster)

standalone 模式启动 `helm install my-redis bitnami/redis --version 18.5.0 --set architecture=standalone --set-string auth.password=123456 --set master.persistence.size=1Gi` 

sentinel 模式启动 `helm install my-redis-sentinel bitnami/redis --version 18.5.0 --set architecture=replication  --set sentinel.enabled=true   --set-string auth.password=123456  --set master.persistence.size=1Gi `

cluster 模式启动 `helm install my-redis-cluster bitnami/redis-cluster --version 9.1.4 --set-string global.redis.password=123456  --set persistence.size=1Gi --set cluster.nodes=6`

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

redis-template 底层默认使用的lettuce,根据redis集群部署模式,需要使用不同配的配置方式进行拓扑发现

#### 哨兵模式

哨兵（sentinel）模式可以参考 test 下 RedisTemplateSentinelDemo,配置文件如下

```yaml
spring:
  redis:
    database: 0
    sentinel:
      nodes: my-redis-sentinel.default:26379 # 添加多个redis哨兵地址,用逗号分割。注意不是写主备节点地址
      master: mymaster # 哨兵配置中的集群名,配置文件sentinel.conf 中 sentinel monitor mymaster 127.0.0.1 6379 2
      password: "123456" # 哨兵密码

    lettuce:
      cluster:
        refresh:
          period: 2000 # 定时刷新周期
          adaptive: true # 自适应拓扑刷新
    connect-timeout: 30000
    password: "123456" # redis密码,哨兵模式redis密码和哨兵密码都要填写
```

#### 集群模式

集群（cluster）模式可以参考 test 下 RedisTemplateClusterDemo，配置文件参考如下

```yaml
spring:
  redis:
    password: "123456"
    database: 0
    cluster:
      nodes: my-redis-cluster.default:6379 # 集群模式下 redis 地址,多个用逗号分隔
    lettuce:
      cluster:
        refresh:
          period: 2000 # 定时刷新周期
          adaptive: true # 自适应拓扑刷新

```

## Redission

redisson 客户端封装了大量基于redis实现的高级特性，例如分布式锁和同步器、分布式对象、分布式集合、web session 管理等，一般常用作分布式锁

### lock

#### lock

LockDemo.java 演示了分布式锁的最基本用法，设置了自动释放锁的时间

#### CountDownLatch

CountDownLatch 常用于主线程等待一个或多个子任务。使用时会初始化一个值count，可以调用方法让count减少，当count为0时，唤醒调用了await方法的线程

CountDownLatchDemo.java 演示了一个线程等待其他线程完成任务再执行

### objects

#### Bucket

Bucket能方便地存储任何对象

BucketDemo.java：演示了如何把一个User对象使用JacksonCodec序列化方式存储到Redis中，并反序列化读取

#### 限速器 RateLimiter

令牌桶限速：模拟一定容量的桶，每隔固定时间就会添加一个新的令牌到桶中，每来一个请求就从桶取出一个令牌，如果没有令牌就会被阻塞。

redission只实现**部分**令牌桶算法，不能设置桶的总大小，只允许设置n秒生成m个令牌，令牌上限是m。

RateLimiterDemo.java：演示设置了一个全局令牌桶，每2秒生成5个令牌

#### 布隆过滤器 Bloom Filter

布隆过滤器可以检测一个元素在不在集合里，会有一定的误报率

#### HyperLogLog

HyperLogLog用来估算统计海量的不重复元素的数量，而且只需要很小的内存，误差范围在2%以内

常用于：

+ 统计网站UV
+ 大数据分析中基数统计

HyperLogLogDemo.java：演示了HyperLogLog的基本使用

### service



#### Executor Service

Redission 提供分布式服务功能，这里引入 RedissionNode 的概念，RedissionNode是用于执行分布式任务，如果把节点分为控制节点、工作节点，RedssionNode就是执行任务的工作节点

Redisson的分布式执行服务实现了`java.util.concurrent.ExecutorService`接口，支持在不同的[独立节点](https://github.com/redisson/redisson/wiki/12.-独立节点模式)（RedissionNode）里执行基于`java.util.concurrent.Callable`接口或`java.lang.Runnable`接口或`Lambda`的任务

控制节点提交任务时，会把整个 Runnable或Callable 任务Class信息转化并存放到 Redission中，工作节点自动使用ClassLoader加载这些信息，因此大部分情况下不要求任务Class在工作节点类路径中

executor/ExecutorServiceDemo.java：演示了获取线程池，并向线程池提交12个EchoTask 任务

executor/RedissionNodeDemo.java：演示了把自身注册为工作节点，并且设置对于`myExecutor0`服务，当前工作节点有1个工作线程

executor/RedissionNodeDemo2.java：演示了把自身注册为工作节点，并且设置对于`myExecutor0`服务，当前工作节点有2个工作线程

依次运行RedissionNodeDemo、RedissionNodeDemo2、ExecutorServiceDemo，可以看到提交的12个任务，有8个在工作节点2执行，有4个在工作节点1执行

#### Scheduler Service

Redisson提供分布式调度任务服务，我们可以提交一些定时任务、特定时间节点需要执行的任务，RedissonNode节点会对这些任务进行消费，可以控制一个任务在一个时间点只会被执行一次。比起xxl-job等分布式调度任务组件，使用redission可以让服务自身具有动态创建定时任务、管理定时任务的能力，缺点都由程序管理，不提供可视化能力。

scheduler/SchedulerServiceDemo.java：演示了获取线程池，并向线程池中提交了一个id为`taskId` 的定时任务，该任务每五秒被执行一次

scheduler/SchedulerServiceCancelDemo.java：演示了取消线程池中id为`taskId`的定时任务

依次运行RedissionNodeDemo、RedissionNodeDemo2、SchedulerServiceDemo，可以看到每五秒在两个RedissonNode节点中执行一次任务，再执行SchedulerServiceCancelDemo取消任务




## 参考

[Redisson PRO - Ultimate Redis Java client with features of In-Memory Data Grid](https://redisson.pro/)

[从原理到实践，五分钟时间带你了解 Redisson 分布式锁的实现方案 - 掘金 (juejin.cn)](https://juejin.cn/post/7294563074937061387?searchId=202401071938048692477E976EB5C89765)

[GitHub - redisson/redisson-examples: Redisson java examples](https://github.com/redisson/redisson-examples)

[目录 · redisson/redisson Wiki · GitHub](https://github.com/redisson/redisson/wiki/目录)