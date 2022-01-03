package top.gumt.mall.order.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import top.gumt.common.utils.R;
import top.gumt.mall.order.vo.FareVo;
import top.gumt.mall.order.vo.WareSkuLockVo;
import top.gumt.common.vo.SkuHasStockVO;

import java.util.List;

@FeignClient("mall-ware")
public interface WareFeignService {
    @RequestMapping("ware/waresku/getSkuHasStocks")
    List<SkuHasStockVO> getSkuHasStocks(@RequestBody List<Long> ids);

    @RequestMapping("ware/wareinfo/fare/{addrId}")
    FareVo getFare(@PathVariable("addrId") Long addrId);

    @RequestMapping("ware/waresku/lock/order")
    R orderLockStock(@RequestBody WareSkuLockVo itemVos);
}
