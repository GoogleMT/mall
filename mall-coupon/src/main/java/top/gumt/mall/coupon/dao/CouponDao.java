package top.gumt.mall.coupon.dao;

import top.gumt.mall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author zhaoming
 * @email gumt0310@gmail.com
 * @date 2021-07-15 20:59:34
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
