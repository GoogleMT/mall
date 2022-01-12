package top.gumt.mall.ware.listener;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.gumt.common.mq.OrderTo;
import top.gumt.common.mq.StockLockedTo;
import top.gumt.mall.ware.service.WareSkuService;

import java.io.IOException;

@Slf4j
@Component
@RabbitListener(queues = {"stock.release.stock.queue"})
public class StockReleaseListener {
    @Autowired
    private WareSkuService wareSkuService;

    /**
     *  解锁：
     *      1、查询数据库关于这个订单的锁定库存信息
     *      有：证明库存锁定成功了
     *          解锁：订单情况
     *              1、没有这个订单，必须解锁
     *              2、有这个订单，不是解锁库存
     *                  订单状态： 已取消：解锁库存
     *                            没取消：不能解锁
     *
     *       没有：库存锁定失败了，库存回滚了。这种情况无需解锁
     */
    @RabbitHandler
    public void handleStockLockedRelease(StockLockedTo stockLockedTo, Message message, Channel channel) throws IOException {
        log.info("************************收到库存解锁的消息********************************");
        try {
            wareSkuService.unlock(stockLockedTo);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }
    }

    @RabbitHandler
    public void handleStockLockedRelease(OrderTo orderTo, Message message, Channel channel) throws IOException {
        log.info("************************从订单模块收到库存解锁的消息********************************");
        try {
            wareSkuService.unlock(orderTo);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }
    }


}
