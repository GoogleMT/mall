package top.gumt.mall.order.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import top.gumt.mall.order.vo.OrderItemVo;

import java.util.List;

@FeignClient("mall-cart")
public interface CartFeignService {

    @ResponseBody
    @RequestMapping("/getCheckedItems")
    List<OrderItemVo> getCheckedItems();

    @ResponseBody
    @GetMapping("/currentUserCartItems")
    List<OrderItemVo> getCurrentUserCartItems();
}
