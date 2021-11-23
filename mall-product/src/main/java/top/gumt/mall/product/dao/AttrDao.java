package top.gumt.mall.product.dao;

import org.apache.ibatis.annotations.Param;
import top.gumt.mall.product.entity.AttrEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 商品属性
 * 
 * @author zhaoming
 * @email gumt0310@gmail.com
 * @date 2021-07-15 00:27:48
 */
@Mapper
public interface AttrDao extends BaseMapper<AttrEntity> {

    /**
     *
     * @param attrIds
     * @return
     */
    List<Long> selectSearchAttrs(@Param("attrIds") List<Long> attrIds);
}
