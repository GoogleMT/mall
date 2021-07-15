package top.gumt.mall.product.dao;

import top.gumt.mall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author zhaoming
 * @email gumt0310@gmail.com
 * @date 2021-07-15 00:27:48
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
