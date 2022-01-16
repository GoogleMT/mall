package top.gumt.mall.seckill;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@SpringBootTest
class MallSeckillApplicationTests {

    @Test
    void contextLoads() {
        LocalDate now = LocalDate.now();
        LocalTime min = LocalTime.MIN;
        System.out.println(min);
        LocalDateTime start = LocalDateTime.of(now, min);
        System.out.println(start);
    }

}
