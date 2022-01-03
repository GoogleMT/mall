package top.gumt.mall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.gumt.common.utils.PageUtils;
import top.gumt.mall.member.entity.MemberReceiveAddressEntity;

import java.util.List;
import java.util.Map;

/**
 * 会员收货地址
 *
 * @author zhaoming
 * @email gumt0310@gmail.com
 * @date 2021-07-15 21:14:31
 */
public interface MemberReceiveAddressService extends IService<MemberReceiveAddressEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 通过用户ID 获取用户的收货地址
     * @param userId
     * @return
     */
    List<MemberReceiveAddressEntity> getAddressByUserId(Long userId);
}

