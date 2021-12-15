package top.gumt.mall.product.dao;

import top.gumt.mall.product.entity.AttrGroupEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.gumt.mall.product.vo.SpuItemAttrGroupVo;

import java.util.List;

/**
 * 属性分组
 * 
 * @author zhaoming
 * @email gumt0310@gmail.com
 * @date 2021-07-15 00:27:48
 */
@Mapper
public interface AttrGroupDao extends BaseMapper<AttrGroupEntity> {

    List<SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuId(Long spuId, Long catalogId);
}
