package top.gumt.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.gumt.common.utils.PageUtils;
import top.gumt.mall.product.entity.CategoryEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author zhaoming
 * @email gumt0310@gmail.com
 * @date 2021-07-15 00:27:48
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryEntity> listWithTree();
}

