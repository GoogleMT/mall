package top.gumt.mall.cart.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import top.gumt.common.utils.R;

import java.math.BigDecimal;
import java.util.List;

@FeignClient("mall-product")
public interface ProductFeignService {
    @RequestMapping("product/skuinfo/info/{skuId}")
    R info(@PathVariable("skuId") Long skuId);

    @RequestMapping("product/skusaleattrvalue/getSkuSaleAttrValuesAsString")
    List<String> getSkuSaleAttrValuesAsString(@RequestBody Long skuId);

    /**
     * 远程调用获取商品价格
     * @param skuId
     * @return
     */
    @GetMapping("/price/{skuId}")
    R getPrice(@PathVariable("skuId") Long skuId);
}
