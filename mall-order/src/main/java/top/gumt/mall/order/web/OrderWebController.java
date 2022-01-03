package top.gumt.mall.order.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import top.gumt.mall.order.service.OrderService;
import top.gumt.mall.order.vo.OrderConfirmVo;

@Controller
public class OrderWebController {

    @Autowired
    OrderService orderService;

    @RequestMapping("/toTrade")
    public String toTrade(Model model) {
        OrderConfirmVo confirmVo = orderService.confirmOrder();
        model.addAttribute("confirmOrder", confirmVo);
        return "confirm";
    }

}
