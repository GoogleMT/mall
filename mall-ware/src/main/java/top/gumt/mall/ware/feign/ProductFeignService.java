package top.gumt.mall.ware.feign;

import feign.Request;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import top.gumt.common.utils.R;

@FeignClient(value = "mall-product")
public interface ProductFeignService {

    @GetMapping(value = "/product/skuinfo/info/{skuId}")
    public R info(@PathVariable("skuId") Long skuId);
}
