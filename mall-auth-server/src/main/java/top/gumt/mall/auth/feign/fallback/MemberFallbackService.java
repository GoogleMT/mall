package top.gumt.mall.auth.feign.fallback;

import org.springframework.stereotype.Service;
import top.gumt.common.exception.BizCodeEnum;
import top.gumt.common.utils.R;
import top.gumt.mall.auth.feign.MemberFeignService;
import top.gumt.mall.auth.vo.SocialUser;
import top.gumt.mall.auth.vo.UserLoginVo;
import top.gumt.mall.auth.vo.UserRegisterVo;

@Service
public class MemberFallbackService implements MemberFeignService {

    @Override
    public R register(UserRegisterVo registerVo) {
        return R.error(BizCodeEnum.READ_TIME_OUT_EXCEPTION.getCode(), BizCodeEnum.READ_TIME_OUT_EXCEPTION.getMsg());
    }

    @Override
    public R login(UserLoginVo loginVo) {
        return R.error(BizCodeEnum.READ_TIME_OUT_EXCEPTION.getCode(), BizCodeEnum.READ_TIME_OUT_EXCEPTION.getMsg());
    }

    @Override
    public R login(SocialUser socialUser) {
        return R.error(BizCodeEnum.READ_TIME_OUT_EXCEPTION.getCode(), BizCodeEnum.READ_TIME_OUT_EXCEPTION.getMsg());
    }
}
