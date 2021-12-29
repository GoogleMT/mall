package top.gumt.mall.cart.service;

import top.gumt.mall.cart.vo.CartItemVo;
import top.gumt.mall.cart.vo.CartVo;

public interface CartService {

    /**
     * 添加购物车
     * @param skuId
     * @param num
     * @return
     */
    CartItemVo addCartItem(Long skuId, Integer num);

    /**
     * 获取购物车
     * @return
     */
    CartVo getCart();

    /**
     * 获取单独的一个商品
     * @param skuId
     * @return
     */
    CartItemVo getCartItem(Long skuId);
}
