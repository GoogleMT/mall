package top.gumt.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.gumt.common.mq.OrderTo;
import top.gumt.common.mq.StockLockedTo;
import top.gumt.common.utils.PageUtils;
import top.gumt.mall.ware.entity.WareSkuEntity;
import top.gumt.mall.ware.vo.SkuHasStockVO;
import top.gumt.mall.ware.vo.WareSkuLockVo;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author zhaoming
 * @email gumt0310@gmail.com
 * @date 2021-07-15 22:11:56
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addStock(Long skuId, Long wareId, Integer skuNum);

    /**
     * 用于检查每一个商品的库存
     * @param skuIds
     * @return
     */
    List<SkuHasStockVO> getSkusHasStock(List<Long> skuIds);

    List<SkuHasStockVO> getSkuHasStocks(List<Long> ids);

    /**
     * 锁
     * @param lockVo
     * @return
     */
    Boolean orderLockStock(WareSkuLockVo lockVo);

    /**
     * 解锁
     * @param stockLockedTo
     */
    void unlock(StockLockedTo stockLockedTo);

    void unlock(OrderTo orderTo);
}

