package top.gumt.mall.product.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import top.gumt.common.utils.R;
import top.gumt.mall.product.feign.config.SeckFeignConfiguration;
import top.gumt.mall.product.feign.fallback.SeckillFallbackService;

// , configuration = SeckFeignConfiguration.class
@FeignClient(value = "mall-seckill",fallback = SeckillFallbackService.class)
public interface SeckillFeignService {

    @ResponseBody
    @GetMapping(value = "/getSeckillSkuInfo/{skuId}")
    R getSeckillSkuInfo(@PathVariable("skuId") Long skuId);

}
