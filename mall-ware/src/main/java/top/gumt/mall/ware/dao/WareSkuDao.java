package top.gumt.mall.ware.dao;

import org.apache.ibatis.annotations.Param;
import top.gumt.mall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品库存
 * 
 * @author zhaoming
 * @email gumt0310@gmail.com
 * @date 2021-07-15 22:11:56
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

    void addStock(Long skuId, Long wareId, Integer skuNum);

    /**
     *
     * @param skuId
     * @return
     */
    Long getSkuStock(@Param("skuId") Long skuId);
}
