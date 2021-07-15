package top.gumt.mall.coupon;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.gumt.mall.coupon.service.CouponService;

@SpringBootTest
class MallCouponApplicationTests {
    @Autowired
    CouponService couponService;

    @Test
    void contextLoads() {
        int count = couponService.count();
        System.out.println(count);
    }

}
