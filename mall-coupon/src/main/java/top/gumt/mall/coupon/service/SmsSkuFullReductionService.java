package top.gumt.mall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.gumt.common.to.SkuReductionTo;
import top.gumt.common.utils.PageUtils;
import top.gumt.mall.coupon.entity.SmsSkuFullReductionEntity;

import java.util.Map;

/**
 * 商品满减信息
 *
 * @author zhaoming
 * @email gumt0310@gmail.com
 * @date 2021-07-15 20:45:27
 */
public interface SmsSkuFullReductionService extends IService<SmsSkuFullReductionEntity> {

    PageUtils queryPage(Map<String, Object> params);

}

