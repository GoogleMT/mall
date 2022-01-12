package top.gumt.mall.order.web;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import top.gumt.mall.order.entity.OrderEntity;

import java.util.Date;
import java.util.UUID;

@Controller
public class HelloController {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/test/createOrder")
    public String testSubmitOrder() {
        //订单下单成功
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(UUID.randomUUID().toString());
        orderEntity.setModifyTime(new Date());

        rabbitTemplate.convertAndSend("order-event-exchange","order.create.order",orderEntity);
        return "ok";
    }
}
