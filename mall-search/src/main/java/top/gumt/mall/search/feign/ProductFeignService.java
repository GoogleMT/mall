package top.gumt.mall.search.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.gumt.common.utils.R;

import java.util.List;

@FeignClient("mall-product")
public interface ProductFeignService {

    /**
     * 信息
     * 功能：查询属性详情
     */
    @RequestMapping("/product/attr/info/{attrId}")
    public R info(@PathVariable("attrId") Long attrId);

    /**
     * @param barndId
     * @return
     */
    @GetMapping("/product/brand/infos")
    public R brandsInfo(@RequestParam("brandIds") List<Long> barndId);

}
