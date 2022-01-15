package top.gumt.mall.seckill.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

// 开启异步的支持, 防止异步任务之间相互堵塞
@EnableAsync
// 开启对定时任务的支持
@EnableScheduling
@Configuration
public class ScheduledConfig {



}
