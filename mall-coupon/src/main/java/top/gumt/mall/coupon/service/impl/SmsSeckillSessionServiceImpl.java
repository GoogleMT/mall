package top.gumt.mall.coupon.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.gumt.common.utils.PageUtils;
import top.gumt.common.utils.Query;

import top.gumt.mall.coupon.dao.SmsSeckillSessionDao;
import top.gumt.mall.coupon.entity.SmsSeckillSessionEntity;
import top.gumt.mall.coupon.service.SmsSeckillSessionService;


@Service("smsSeckillSessionService")
public class SmsSeckillSessionServiceImpl extends ServiceImpl<SmsSeckillSessionDao, SmsSeckillSessionEntity> implements SmsSeckillSessionService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SmsSeckillSessionEntity> page = this.page(
                new Query<SmsSeckillSessionEntity>().getPage(params),
                new QueryWrapper<SmsSeckillSessionEntity>()
        );

        return new PageUtils(page);
    }

}