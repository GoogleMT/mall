package top.gumt.mall.product.web;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import top.gumt.mall.product.service.SkuInfoService;
import top.gumt.mall.product.vo.SkuItemVo;

@Slf4j
@Controller
public class ItemController {

    @Autowired
    SkuInfoService skuInfoService;

    /**
     * 根据skuId取得商品的详情信息
     * @param skuId
     * @return
     */
    @GetMapping("/{skuId}.html")
    public String skuItem(@PathVariable("skuId") Long skuId, Model model){
        System.out.println("查询商品信息： " + skuId);
        SkuItemVo vo=skuInfoService.item(skuId);
        model.addAttribute("item",vo);
        return "item";
    }
}
