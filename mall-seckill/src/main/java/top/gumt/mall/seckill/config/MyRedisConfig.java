package top.gumt.mall.seckill.config;


import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyRedisConfig {
    @Value("${ipAddr}")
    private String ipAddr;

    /**
     * redission通过redissonClient 对象使用
     * 如果是多个redis集群，可以配置
     * @return
     */
    @Bean(destroyMethod = "shutdown")
    public RedissonClient redisClient() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + ipAddr + ":6379");
        RedissonClient redissonClient = Redisson.create(config);
        return redissonClient;
    }
}
