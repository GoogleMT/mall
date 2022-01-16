package top.gumt.mall.product.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import top.gumt.common.utils.R;
import top.gumt.mall.product.feign.fallback.SeckillFallbackService;

@FeignClient(value = "mall-seckill",fallback = SeckillFallbackService.class)
public interface SeckillFeignService {

    @ResponseBody
    @GetMapping(value = "/getSeckillSkuInfo/{skuId}")
    R getSeckillSkuInfo(@PathVariable("skuId") Long skuId);
}
