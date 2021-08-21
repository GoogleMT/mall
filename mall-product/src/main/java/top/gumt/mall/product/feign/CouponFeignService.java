package top.gumt.mall.product.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import top.gumt.common.to.SkuReductionTo;
import top.gumt.common.to.SpuBoundTo;
import top.gumt.common.utils.R;

@FeignClient("mall-coupon")
public interface CouponFeignService {

    /**
     * 1. CouponFeignService.saveSpuBounds(spuBoundTo)
     *      1.@RequestBody将这个对象转为json
     *      2.找到mall-coupon服务，给/coupon/spubounds/save发送请求
     *        将上一步转的json放在请求位置，发送请求
     *      3.对方服务收到请求，请求体里有json数据
     *          （@RequestBody SpuBoundsEntity spuBounds） 将请求体的json转为SpuBoundsEntity；
     *         只要json数据模型是兼容的，双方服务无需使用同一个to
     * @param spuBoundTo
     * @return
     */
    @PostMapping("/coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundTo spuBoundTo);

    @PostMapping("/coupon/skufullreduction/saveInfo")
    R saveSkuReduction(@RequestBody SkuReductionTo skuReductionTo);
    }
