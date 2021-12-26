package top.gumt.mall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.gumt.common.utils.PageUtils;
import top.gumt.mall.member.entity.MemberEntity;
import top.gumt.mall.member.exception.PhoneNumExistException;
import top.gumt.mall.member.exception.UserExistException;
import top.gumt.mall.member.vo.MemberLoginVo;
import top.gumt.mall.member.vo.MemberRegisterVo;
import top.gumt.mall.member.vo.SocialUser;

import java.util.Map;

/**
 * 会员
 *
 * @author zhaoming
 * @email gumt0310@gmail.com
 * @date 2021-07-15 21:14:31
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 注册
     * @param registerVo
     * @throws PhoneNumExistException
     * @throws UserExistException
     */
    void register(MemberRegisterVo registerVo) throws PhoneNumExistException, UserExistException;

    /**
     * 账号密码登录
     * @param loginVo
     * @return
     */
    MemberEntity login(MemberLoginVo loginVo);

    MemberEntity login(SocialUser socialUser);
}

