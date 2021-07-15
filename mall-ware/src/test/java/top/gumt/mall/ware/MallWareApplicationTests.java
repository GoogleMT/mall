package top.gumt.mall.ware;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.gumt.mall.ware.service.PurchaseService;

@SpringBootTest
class MallWareApplicationTests {

    @Autowired
    PurchaseService purchaseService;

    @Test
    void contextLoads() {
        int count = purchaseService.count();
        System.out.println(count);
    }

}
