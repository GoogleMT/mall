package top.gumt.mall.product.feign.fallback;

import org.springframework.stereotype.Component;
import top.gumt.common.exception.BizCodeEnum;
import top.gumt.common.utils.R;
import top.gumt.mall.product.feign.SeckillFeignService;

@Component
public class SeckillFallbackService implements SeckillFeignService {
    @Override
    public R getSeckillSkuInfo(Long skuId) {
        return R.error(BizCodeEnum.READ_TIME_OUT_EXCEPTION.getCode(), BizCodeEnum.READ_TIME_OUT_EXCEPTION.getMsg());
    }
}
