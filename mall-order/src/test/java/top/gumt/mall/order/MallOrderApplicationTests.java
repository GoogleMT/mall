package top.gumt.mall.order;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.gumt.mall.order.entity.OrderEntity;
import top.gumt.mall.order.entity.OrderReturnReasonEntity;
import top.gumt.mall.order.service.OrderService;

import java.util.Date;
import java.util.UUID;

@Slf4j
@SpringBootTest
class MallOrderApplicationTests {
    @Autowired
    OrderService orderService;

    @Autowired
    AmqpAdmin amqpAdmin;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Test
    void contextLoads() {
        int count = orderService.count();
        System.out.println(count);
    }

    @Test
    void sendMessage() {
        // 1.发送消息
//        rabbitTemplate.convertAndSend("hello.java.exchange", "hello.java", "hello world!");
//        log.info("消息发送完成{}", "hello world!");

//        OrderReturnReasonEntity reasonEntity = new OrderReturnReasonEntity();
//        reasonEntity.setId(1L);
//        reasonEntity.setCreateTime(new Date());
//        reasonEntity.setName("测试");
//        rabbitTemplate.convertAndSend("hello.java.exchange", "hello.java", reasonEntity);
//        log.info("消息发送完成{}", reasonEntity);
        for (int i = 0; i < 10; i++) {
            if(i % 2 == 0) {
                OrderReturnReasonEntity reasonEntity = new OrderReturnReasonEntity();
                reasonEntity.setId(1L);
                reasonEntity.setCreateTime(new Date());
                reasonEntity.setName("测试");
                rabbitTemplate.convertAndSend("hello.java.exchange", "hello.java", reasonEntity);
                log.info("消息发送完成{}", reasonEntity);
            } else {
                OrderEntity orderEntity = new OrderEntity();
                orderEntity.setId(1L);
                orderEntity.setMemberId(2L);
                orderEntity.setOrderSn(UUID.randomUUID().toString());
                rabbitTemplate.convertAndSend("hello.java.exchange", "hello.java", orderEntity);
                log.info("消息发送完成{}", orderEntity);
            }
        }

    }

    /**
     * // 全参构造器： DirectExchange(String name, boolean durable, boolean autoDelete, Map<String, Object> arguments)
     */
    @Test
    void createExchange() {
        DirectExchange directExchange = new DirectExchange("hello.java.exchange", true, false);
        amqpAdmin.declareExchange(directExchange);
        log.info("Exchange [{}] 创建成功", "hello.java.exchange");
    }

    @Test
    void createQueue () {
        Queue queue = new Queue("hello-java-queue", true, false, false);
        amqpAdmin.declareQueue(queue);
        log.info("Queue[{}]创建成功", "hello-java-queue");
    }

    @Test
    void createBinding() {
        /**
         * Binding(String destination, Binding.DestinationType destinationType, String exchange, String routingKey, @Nullable Map<String, Object> arguments)
         * destination  目的地
         * DestinationType destinationType 目的地类型
         * String routingKey 路由键
         * Map<String, Object> arguments 自定义参数
         * 将exchange 指定的交换机和destination目的地进行板顶，使用routingKey作为指定路由键
         */
        Binding binding = new Binding("hello-java-queue",
                Binding.DestinationType.QUEUE,
                "hello.java.exchange",
                "hello.java", null);
        amqpAdmin.declareBinding(binding);
        log.info("Binding[{}]创建成功", "hello-java-binding");
    }

}
