package top.gumt.mall.ware.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import top.gumt.common.exception.NoStockException;
import top.gumt.common.utils.PageUtils;
import top.gumt.common.utils.Query;

import top.gumt.common.utils.R;
import top.gumt.mall.ware.dao.WareSkuDao;
import top.gumt.mall.ware.entity.WareOrderTaskDetailEntity;
import top.gumt.mall.ware.entity.WareOrderTaskEntity;
import top.gumt.mall.ware.entity.WareSkuEntity;
import top.gumt.mall.ware.feign.ProductFeignService;
import top.gumt.mall.ware.service.WareSkuService;
import top.gumt.mall.ware.vo.OrderItemVo;
import top.gumt.mall.ware.vo.SkuHasStockVO;
import top.gumt.mall.ware.vo.WareSkuLockVo;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    ProductFeignService productFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        QueryWrapper<WareSkuEntity> queryWrapper = new QueryWrapper<>();
        String skuId = (String) params.get("skuId");
        if(StringUtils.isNotEmpty(skuId)) {
            queryWrapper.eq("sku_id", skuId);
        }

        String wareId = (String) params.get("wareId");
        if(StringUtils.isNotEmpty(wareId)){
            queryWrapper.eq("ware_id", wareId);
        }

        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        List<WareSkuEntity> wareSkuEntities = this.baseMapper.selectList(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));
        if(wareSkuEntities == null || wareSkuEntities.size() == 0 ){
            //新增
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setStockLocked(0);
            //远程查询SKU的name，若失败无需回滚
            try {
                R info = productFeignService.info(skuId);
                if(info.getCode() == 0){
                    Map<String,Object> data=(Map<String,Object>)info.get("skuInfo");
                    wareSkuEntity.setSkuName((String) data.get("skuName"));
                }
            } catch (Exception e) {
            }
            this.baseMapper.insert(wareSkuEntity);
        }else{
            //插入
            this.baseMapper.addStock(skuId,wareId,skuNum);
        }
    }

    @Override
    public List<SkuHasStockVO> getSkusHasStock(List<Long> skuIds) {
        List<SkuHasStockVO> collect = skuIds.stream().map(skuId -> {
            SkuHasStockVO hasStockVO = new SkuHasStockVO();
            // 查询当前sku的库存量
            // SELECT SUM(stock - stock_locked) FROM wms_ware_sku WHERE sku_id = 3
            Long count = baseMapper.getSkuStock(skuId);
            hasStockVO.setHasStock(count == null ? false : count > 0);
            hasStockVO.setSkuId(skuId);
            return hasStockVO;
        }).collect(Collectors.toList());
        return collect;
    }

    @Override
    public List<SkuHasStockVO> getSkuHasStocks(List<Long> ids) {
        List<SkuHasStockVO> skuHasStockVos = ids.stream().map(id -> {
            SkuHasStockVO skuHasStockVo = new SkuHasStockVO();
            skuHasStockVo.setSkuId(id);
            Integer count = baseMapper.getTotalStock(id);
            skuHasStockVo.setHasStock(count==null?false:count>0);
            return skuHasStockVo;
        }).collect(Collectors.toList());
        return skuHasStockVos;
    }

    @Transactional
    @Override
    public Boolean orderLockStock(WareSkuLockVo wareSkuLockVo) {
        //因为可能出现订单回滚后，库存锁定不回滚的情况，但订单已经回滚，得不到库存锁定信息，因此要有库存工作单
//        WareOrderTaskEntity taskEntity = new WareOrderTaskEntity();
//        taskEntity.setOrderSn(wareSkuLockVo.getOrderSn());
//        taskEntity.setCreateTime(new Date());
//        wareOrderTaskService.save(taskEntity);
//
//        List<OrderItemVo> itemVos = wareSkuLockVo.getLocks();
//        List<SkuLockVo> lockVos = itemVos.stream().map((item) -> {
//            SkuLockVo skuLockVo = new SkuLockVo();
//            skuLockVo.setSkuId(item.getSkuId());
//            skuLockVo.setNum(item.getCount());
//            //找出所有库存大于商品数的仓库
//            List<Long> wareIds = baseMapper.listWareIdsHasStock(item.getSkuId(), item.getCount());
//            skuLockVo.setWareIds(wareIds);
//            return skuLockVo;
//        }).collect(Collectors.toList());
//
//        for (SkuLockVo lockVo : lockVos) {
//            boolean lock = true;
//            Long skuId = lockVo.getSkuId();
//            List<Long> wareIds = lockVo.getWareIds();
//            //如果没有满足条件的仓库，抛出异常
//            if (wareIds == null || wareIds.size() == 0) {
//                throw new NoStockException(skuId);
//            }else {
//                for (Long wareId : wareIds) {
//                    Long count=baseMapper.lockWareSku(skuId, lockVo.getNum(), wareId);
//                    if (count==0){
//                        lock=false;
//                    }else {
//                        //锁定成功，保存工作单详情
//                        WareOrderTaskDetailEntity detailEntity = WareOrderTaskDetailEntity.builder()
//                                .skuId(skuId)
//                                .skuName("")
//                                .skuNum(lockVo.getNum())
//                                .taskId(taskEntity.getId())
//                                .wareId(wareId)
//                                .lockStatus(1).build();
//                        wareOrderTaskDetailService.save(detailEntity);
//                        //发送库存锁定消息至延迟队列
//                        StockLockedTo lockedTo = new StockLockedTo();
//                        lockedTo.setId(taskEntity.getId());
//                        StockDetailTo detailTo = new StockDetailTo();
//                        BeanUtils.copyProperties(detailEntity,detailTo);
//                        lockedTo.setDetailTo(detailTo);
//                        rabbitTemplate.convertAndSend("stock-event-exchange","stock.locked",lockedTo);
//
//                        lock = true;
//                        break;
//                    }
//                }
//            }
//            if (!lock) {
//                throw new NoStockException(skuId);
//            }
//        }
        return true;
    }
}