package top.gumt.mall.seckill.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HelloSchedule {

    @Scheduled(cron = "0 0 3 * * ?")
    @Async
    public void hello() throws InterruptedException {
        log.info("hello...");
    }
}
