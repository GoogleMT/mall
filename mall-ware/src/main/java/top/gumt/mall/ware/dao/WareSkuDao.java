package top.gumt.mall.ware.dao;

import org.apache.ibatis.annotations.Param;
import top.gumt.mall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 商品库存
 * 
 * @author zhaoming
 * @email gumt0310@gmail.com
 * @date 2021-07-15 22:11:56
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {
    /**
     *
     * @param skuId
     * @param wareId
     * @param skuNum
     */
    void addStock(Long skuId, Long wareId, Integer skuNum);

    /**
     *
     * @param skuId
     * @return
     */
    Long getSkuStock(@Param("skuId") Long skuId);

    /**
     *
     * @param id
     * @return
     */
    Integer getTotalStock(@Param("id") Long id);

    /**
     * 找出所有库存大于商品数的仓库
     * @param skuId
     * @param count
     * @return
     */
    List<Long> listWareIdsHasStock(@Param("skuId") Long skuId, @Param("count") Integer count);

    /**
     * 锁住商品
     * @param skuId
     * @param num
     * @param wareId
     * @return
     */
    Long lockWareSku(@Param("skuId") Long skuId, @Param("num") Integer num, @Param("wareId") Long wareId);

    /**
     * 解锁商品
     * @param skuId
     * @param skuNum
     * @param wareId
     */
    void unlockStock(@Param("skuId") Long skuId, @Param("skuNum") Integer skuNum, @Param("wareId") Long wareId);
}
