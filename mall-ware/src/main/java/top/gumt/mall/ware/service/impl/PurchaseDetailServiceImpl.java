package top.gumt.mall.ware.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.gumt.common.utils.PageUtils;
import top.gumt.common.utils.Query;

import top.gumt.mall.ware.dao.PurchaseDetailDao;
import top.gumt.mall.ware.entity.PurchaseDetailEntity;
import top.gumt.mall.ware.entity.PurchaseEntity;
import top.gumt.mall.ware.service.PurchaseDetailService;


@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailDao, PurchaseDetailEntity> implements PurchaseDetailService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<PurchaseDetailEntity> queryWrapper = new QueryWrapper<>();

        String key=(String)params.get("key");
        if(StringUtils.isNotEmpty(key)){
            queryWrapper.and(w -> {
                w.eq("purchase_id",key).or().eq("sku_id",key);
            });
        }

        String status=(String)params.get("status");
        if(StringUtils.isNotEmpty(status)){
            queryWrapper.eq("status",status);
        }

        String wareId=(String)params.get("wareId");
        if(StringUtils.isNotEmpty(wareId)){
            queryWrapper.eq("ware_id",wareId);
        }


        IPage<PurchaseDetailEntity> page = this.page(
                new Query<PurchaseDetailEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public List<PurchaseDetailEntity> listDetailByPurchaseId(Long id) {
        List<PurchaseDetailEntity> entities = this.baseMapper.selectList(new QueryWrapper<PurchaseDetailEntity>().eq("purchase_id", id));
        return entities;
    }


}