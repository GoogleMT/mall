package top.gumt.mall.product.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.gumt.common.utils.PageUtils;
import top.gumt.common.utils.Query;

import top.gumt.mall.product.dao.BrandDao;
import top.gumt.mall.product.entity.BrandEntity;
import top.gumt.mall.product.service.BrandService;
import top.gumt.mall.product.service.CategoryBrandRelationService;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {
    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String key = (String)params.get("key");
        QueryWrapper<BrandEntity> queryWrapper = new QueryWrapper<>();
        if(StringUtils.isNotEmpty(key)){
            queryWrapper.eq("brand_id",key).or().like("name",key);
        }
        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params),queryWrapper

        );

        return new PageUtils(page);
    }

    @Override
    public void updateDetails(BrandEntity brand) {
        // 保证数据一致
        this.updateById(brand);
        //
        if(StringUtils.isNotEmpty(brand.getName())) {
            // 同步更新其他关联表的数据
            categoryBrandRelationService.updateBrand(brand.getBrandId(), brand.getName());

            // TODO 更新其他关联表数据

        }
    }

    @Override
    public List<BrandEntity> getBrandsByIds(List<Long> barndId) {
        return baseMapper.selectList(new QueryWrapper<BrandEntity>().in("brand_id", barndId));
    }
}