package top.gumt.mall.order.config;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class MyRabbitConfig {
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @PostConstruct
    public void initRabbitTemplate() {
        // 设置确定回调
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            /**
             * 只要消息抵达Broker 就 ack = true
             * @param correlationData 当前消息的唯一关联数据（这个是消息的唯一ID）
             * @param ack 消息是否成功收到
             * @param cause 失败原因
             */
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                /**
                 * 1、做好消息的确认机制 （publisher，consumer【手动ACK】）
                 * 2、每一个发送的消息都在数据库做好记录。定期失败的消息 再次发送一遍
                 */
                System.out.println("confirm...correlationDate[" + correlationData + "]==>ack[" + "]==>cause[" + cause + "]");
            }
        });

        rabbitTemplate.setReturnsCallback(new RabbitTemplate.ReturnsCallback() {
            /**
             * 只要消息没有投递个指定队列，就触发这个失败的回调
             * @param returnedMessage
             */
            @Override
            public void returnedMessage(ReturnedMessage returnedMessage) {
                // ReturnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey)
                // 投递失败的消息详情信息
                Message message = returnedMessage.getMessage();
                // 回复的状态码
                int replyCode = returnedMessage.getReplyCode();
                // 回复的文本内容
                String replyText = returnedMessage.getReplyText();
                // 当时这个消息发给那个交换机
                String exchange = returnedMessage.getExchange();
                // 当时这个消息用那个路由键
                String routingKey = returnedMessage.getRoutingKey();
                System.out.println("message=> [" + message + "]replyCode==>[" + replyCode + "]exchange==>[" + exchange + "]rotingKey==>[" + routingKey + "]");
            }
        });
    }

}
