package top.gumt.mall.cart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import top.gumt.common.constant.AuthServerConstant;
import top.gumt.mall.cart.service.CartService;
import top.gumt.mall.cart.vo.CartItemVo;
import top.gumt.mall.cart.vo.CartVo;

import javax.servlet.http.HttpSession;

@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/test.html")
    public String testHandler() {
        return "test";
    }

    /**
     * 浏览器有一个cookie： user-key 标识用户身份，一个月后过期
     * 如果第一次使用购物车功能，也会给一个临时的用户身份
     * 浏览器以后保存，每次访问都会带上这个session
     * 登录： 有session
     * 没有登录： 按照cookie里面带来的user-key来做
     * 第一次：如果没有临时用户，帮忙创建一个临时用户。
     * @param model
     * @return
     */
    @GetMapping("/cart.html")
    public String cartListPage(Model model) {
        CartVo cartVo=cartService.getCart();
        model.addAttribute("cart", cartVo);
        return "cartList";
    }

    @RequestMapping("/success.html")
    public String success() {
        return "success";
    }


    /**
     * 添加商品到购物车
     * RedirectAttributes.addFlashAttribute():将数据放在session中，可以在页面中取出，但是只能取一次
     * RedirectAttributes.addAttribute():将数据放在url后面
     * @return
     */
    @RequestMapping("/addCartItem")
    public String addCartItem(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num, RedirectAttributes attributes) {
        cartService.addCartItem(skuId, num);
        attributes.addAttribute("skuId", skuId);
        return "redirect:http://cart.mall.com/addCartItemSuccess";
    }

    @RequestMapping("/addCartItemSuccess")
    public String addCartItemSuccess(@RequestParam("skuId") Long skuId, Model model) {
        CartItemVo cartItemVo = cartService.getCartItem(skuId);
        model.addAttribute("cartItem", cartItemVo);
        return "success";
    }
}
