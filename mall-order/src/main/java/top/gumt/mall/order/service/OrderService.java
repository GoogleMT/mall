package top.gumt.mall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.gumt.common.utils.PageUtils;
import top.gumt.mall.order.entity.OrderEntity;
import top.gumt.mall.order.vo.OrderConfirmVo;

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
}

