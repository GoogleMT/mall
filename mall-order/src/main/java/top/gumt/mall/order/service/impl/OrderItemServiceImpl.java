package top.gumt.mall.order.service.impl;

import com.rabbitmq.client.AMQP;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.gumt.common.utils.PageUtils;
import top.gumt.common.utils.Query;

import top.gumt.mall.order.dao.OrderItemDao;
import top.gumt.mall.order.entity.OrderEntity;
import top.gumt.mall.order.entity.OrderItemEntity;
import top.gumt.mall.order.entity.OrderReturnReasonEntity;
import top.gumt.mall.order.service.OrderItemService;


@RabbitListener(queues = {"hello-java-queue"})
@Service("orderItemService")
public class OrderItemServiceImpl extends ServiceImpl<OrderItemDao, OrderItemEntity> implements OrderItemService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderItemEntity> page = this.page(
                new Query<OrderItemEntity>().getPage(params),
                new QueryWrapper<OrderItemEntity>()
        );

        return new PageUtils(page);
    }

    @RabbitHandler
    public void recieveMessage(OrderReturnReasonEntity content) {
        System.out.println("消息体：" + content);
    }

    @RabbitHandler
    public void recieveMessage(OrderEntity content) {
        System.out.println("消息体：" + content);
    }
}