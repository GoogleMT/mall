package top.gumt.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.gumt.common.utils.PageUtils;
import top.gumt.mall.product.entity.BrandEntity;

import java.util.List;
import java.util.Map;

/**
 * 品牌
 *
 * @author zhaoming
 * @email gumt0310@gmail.com
 * @date 2021-07-15 00:27:48
 */
public interface BrandService extends IService<BrandEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void updateDetails(BrandEntity brand);

    /**
     * @param barndId
     * @return
     */
    List<BrandEntity> getBrandsByIds(List<Long> barndId);
}

