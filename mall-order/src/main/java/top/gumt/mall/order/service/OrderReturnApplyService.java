package top.gumt.mall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.gumt.common.utils.PageUtils;
import top.gumt.mall.order.entity.OrderReturnApplyEntity;

import java.util.Map;

/**
 * 订单退货申请
 *
 * @author zhaoming
 * @email gumt0310@gmail.com
 * @date 2021-07-15 21:25:49
 */
public interface OrderReturnApplyService extends IService<OrderReturnApplyEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

