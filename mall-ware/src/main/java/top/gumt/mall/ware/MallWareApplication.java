package top.gumt.mall.ware;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@EnableRabbit
@EnableAspectJAutoProxy(exposeProxy = true)
@EnableTransactionManagement
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "top.gumt.mall.ware.feign")
@MapperScan("top.gumt.mall.ware.dao")
@SpringBootApplication
public class MallWareApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallWareApplication.class, args);
    }

}
