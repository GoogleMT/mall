package top.gumt.mall.order.vo;

import lombok.Data;
import top.gumt.mall.order.entity.OrderEntity;

@Data
public class SubmitOrderResponseVo {
    private OrderEntity order;

    /** 错误状态码 **/
    private Integer code;
}
