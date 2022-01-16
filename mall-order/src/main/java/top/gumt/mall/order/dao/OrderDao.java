package top.gumt.mall.order.dao;

import org.apache.ibatis.annotations.Param;
import top.gumt.mall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author zhaoming
 * @email gumt0310@gmail.com
 * @date 2021-07-15 21:25:49
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {

    /**
     * 更改订单状态
     * @param orderSn
     * @param code
     * @param alipay
     */
    void updateOrderStatus(@Param("orderSn") String orderSn, @Param("code") Integer code, @Param("alipay") Integer alipay);
}
