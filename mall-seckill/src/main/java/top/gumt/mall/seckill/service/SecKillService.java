package top.gumt.mall.seckill.service;


import top.gumt.mall.seckill.to.SeckillSkuRedisTo;

import java.util.List;

public interface SecKillService {
    /**
     * 调用远程方法 上架最近三天的商品
     */
    void uploadSeckillSkuLatest3Days();

    /**
     * 获取到当前可以参加秒杀商品的信息
     * @return
     */
    List<SeckillSkuRedisTo> getCurrentSeckillSkus();

    /**
     * 获取某商品是否 参与秒杀
     * @param skuId
     * @return
     */
    SeckillSkuRedisTo getSeckillSkuInfo(Long skuId);

    /**
     * 商品秒杀
     * @param killId
     * @param key
     * @param num
     * @return
     */
    String kill(String killId, String key, Integer num) throws InterruptedException;
}
