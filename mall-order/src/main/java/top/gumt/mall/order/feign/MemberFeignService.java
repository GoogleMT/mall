package top.gumt.mall.order.feign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import top.gumt.mall.order.vo.MemberAddressVo;

import java.util.List;

@FeignClient("mall-member")
public interface MemberFeignService {
    /**
     * 获取用户的所有收货地址
     * @param userId
     * @return
     */
    @RequestMapping("member/memberreceiveaddress/getAddressByUserId")
    List<MemberAddressVo> getAddressByUserId(@RequestBody Long userId);
}
