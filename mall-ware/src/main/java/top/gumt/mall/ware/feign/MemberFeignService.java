package top.gumt.mall.ware.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import top.gumt.common.utils.R;

@FeignClient("mall-member")
public interface MemberFeignService {
    @RequestMapping("member/memberreceiveaddress/info/{id}")
    R info(@PathVariable("id") Long id);
}
