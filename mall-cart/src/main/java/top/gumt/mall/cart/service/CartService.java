package top.gumt.mall.cart.service;

import top.gumt.mall.cart.vo.CartItemVo;
import top.gumt.mall.cart.vo.CartVo;

import java.util.List;

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

    /**
     * 查看是否被选中
     * @param skuId
     * @param isChecked
     */
    void checkCart(Long skuId, Integer isChecked);

    /**
     *
     * @param skuId
     * @param num
     */
    void changeItemCount(Long skuId, Integer num);

    /**
     * 删除购物车商品
     * @param skuId
     */
    void deleteItem(Long skuId);

    /**
     * 获取选中的商品列表
     * @return
     */
    List<CartItemVo> getCheckedItems();
}
