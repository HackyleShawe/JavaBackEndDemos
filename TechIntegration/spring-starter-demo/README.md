**第一个starter实例**

---

**自定义与实现一个redis-starter，实现自动装配Jedis**

 

**步骤**

- 创建 demo-redis-spring-boot-autoconfigure 模块

  - 初始化 Jedis 的 Bean，需要导入Jedis依赖

  - ETA-INF/spring.factories 文件

- 创建 demo-redis-spring-boot-starter 模块，依赖 redis-spring-bootautoconfigure的模块

- 创建一个test-demo模块，依赖demo-redis-spring-boot-starter

  - 在启动类中获取Spring上下文容器

  - 获取容器中的Jedis实例，对Redis进行操作

 

**模仿SpringBoot Starter**，spring-boot-starter，spring-boot-autoconfigure模块

1)   org.springframework.boot.autoconfigure.data.redis.**RedisAutoConfiguration**

2)   org.springframework.boot.autoconfigure.data.redis.**RedisProperties**