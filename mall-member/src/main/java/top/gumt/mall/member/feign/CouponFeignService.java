package top.gumt.mall.member.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import top.gumt.common.utils.R;

// 告诉spring cloud 这个接口是远程客户端，要调用coupon服务(nacos中找到)，具体是调用coupon服务的/coupon/coupon/member/list对应的方法
@FeignClient("mall-coupon")
public interface CouponFeignService {

    // 远程服务的url
    //注意写全优惠券类上还有映射//注意我们这个地方不是控制层，所以这个请求映射请求的不是我们服务器上的东西，而是nacos注册中心的
    @RequestMapping(value = "/coupon/coupon/member/list")
    public R membercoupons();//得到一个R对象
}
