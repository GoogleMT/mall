package top.gumt.mall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.gumt.common.utils.PageUtils;
import top.gumt.mall.order.entity.OrderEntity;
import top.gumt.mall.order.vo.OrderConfirmVo;
import top.gumt.mall.order.vo.OrderSubmitVo;
import top.gumt.mall.order.vo.PayVo;
import top.gumt.mall.order.vo.SubmitOrderResponseVo;

import java.util.Map;

/**
 * 订单
 *
 * @author zhaoming
 * @email gumt0310@gmail.com
 * @date 2021-07-15 21:25:49
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 封装 订单确认消息
     * @return
     */
    OrderConfirmVo confirmOrder();

    /**
     * 下单操作
     * @param submitVo
     * @return
     */
    SubmitOrderResponseVo submitOrder(OrderSubmitVo submitVo);

    /**
     * 根据订单号获取订单信息
     * @param orderSn
     * @return
     */
    OrderEntity getOrderByOrderSn(String orderSn);

    /**
     * 关闭 订单
     * @param orderEntity
     */
    void closeOrder(OrderEntity orderEntity);

    /**
     *
     * @param orderSn
     * @return
     */
    PayVo getOrderPay(String orderSn);

    /**
     * 获取用户订单
     * @param params
     * @return
     */
    PageUtils getMemberOrderPage(Map<String, Object> params);
}

