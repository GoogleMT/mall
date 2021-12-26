package top.gumt.mall.auth.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import top.gumt.common.utils.R;
import top.gumt.mall.auth.feign.fallback.MemberFallbackService;
import top.gumt.mall.auth.vo.SocialUser;
import top.gumt.mall.auth.vo.UserLoginVo;
import top.gumt.mall.auth.vo.UserRegisterVo;

@FeignClient(value = "mall-member",fallback = MemberFallbackService.class)
public interface MemberFeignService {

    @RequestMapping("member/member/register")
    R register(@RequestBody UserRegisterVo registerVo);


    @RequestMapping("member/member/login")
    R login(@RequestBody UserLoginVo loginVo);

    @RequestMapping("member/member/oauth2/login")
    R login(@RequestBody SocialUser socialUser);
}
