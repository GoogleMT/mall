package top.gumt.mall.order;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.gumt.mall.order.service.OrderService;

@SpringBootTest
class MallOrderApplicationTests {
    @Autowired
    OrderService orderService;

    @Test
    void contextLoads() {
        int count = orderService.count();
        System.out.println(count);
    }

}
