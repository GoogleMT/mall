package top.gumt.mall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.gumt.common.utils.PageUtils;
import top.gumt.mall.order.entity.MqMessageEntity;

import java.util.Map;

/**
 * 
 *
 * @author zhaoming
 * @email gumt0310@gmail.com
 * @date 2021-07-15 21:25:49
 */
public interface MqMessageService extends IService<MqMessageEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

