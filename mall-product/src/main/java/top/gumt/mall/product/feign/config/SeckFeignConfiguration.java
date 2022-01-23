package top.gumt.mall.product.feign.config;

import org.springframework.context.annotation.Bean;
import top.gumt.mall.product.feign.fallback.SeckillFallbackService;

public class SeckFeignConfiguration {
    @Bean
    public SeckillFallbackService echoServiceFallback() {
        return new SeckillFallbackService();
    }
}
