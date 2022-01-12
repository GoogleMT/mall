package top.gumt.mall.ware.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import top.gumt.common.utils.R;

@FeignClient("mall-order")
public interface OrderFeignService {
    @RequestMapping("order/order/status/{orderSn}")
    R infoByOrderSn(@PathVariable("orderSn") String orderSn);
}
