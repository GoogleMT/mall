package top.gumt.mall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import top.gumt.common.constant.ProductConstant;
import top.gumt.common.to.SkuReductionTo;
import top.gumt.common.to.MemberPrice;
import top.gumt.common.to.SpuBoundTo;
import top.gumt.common.to.to.SkuEsModel;
import top.gumt.common.utils.PageUtils;
import top.gumt.common.utils.Query;

import top.gumt.common.utils.R;
import top.gumt.mall.product.dao.SpuInfoDao;
import top.gumt.mall.product.entity.*;
import top.gumt.mall.product.feign.CouponFeignService;
import top.gumt.mall.product.feign.SearchFeignService;
import top.gumt.mall.product.feign.WareFeignService;
import top.gumt.mall.product.service.*;
import top.gumt.mall.product.vo.*;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    SpuInfoDescService spuInfoDescService;

    @Autowired
    SpuImagesService spuImagesService;

    @Autowired
    AttrService attrService;

    @Autowired
    ProductAttrValueService attrValueService;

    @Autowired
    SkuInfoService skuInfoService;

    @Autowired
    SkuImagesService skuImagesService;

    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    CouponFeignService couponFeignService;

    @Autowired
    BrandService brandService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    WareFeignService wareFeignService;

    @Autowired
    SearchFeignService searchFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveVo spuSaveVo) {
        // 1. 保存spu基本信息 pms_spu_info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuSaveVo, spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        spuInfoEntity.setPublishStatus(0);
        this.saveBaseSpuInfo(spuInfoEntity);

        // 2. 保存Spu的描述图片 pms_spu_info_desc
        List<String> decript = spuSaveVo.getDecript();
        SpuInfoDescEntity descEntity = new SpuInfoDescEntity();
        descEntity.setSpuId(spuInfoEntity.getId());
        descEntity.setDecript(String.join(",", decript));
        spuInfoDescService.saveSpuInfoDesc(descEntity);

        // 3. 保存spu的图片集 pms_spu_images
        List<String> images = spuSaveVo.getImages();
        spuImagesService.saveImages(spuInfoEntity.getId(), images);

        // 4. 保存spu的规格参数 pms_product_attr_value
        List<BaseAttrs> baseAttrs = spuSaveVo.getBaseAttrs();
        List<ProductAttrValueEntity> collect = baseAttrs.stream().map(attr -> {
            ProductAttrValueEntity valueEntity = new ProductAttrValueEntity();
            valueEntity.setAttrId(attr.getAttrId());
            AttrEntity id = attrService.getById(attr.getAttrId());
            valueEntity.setAttrName(id.getAttrName());
            valueEntity.setAttrValue(attr.getAttrValues());
            valueEntity.setQuickShow(attr.getShowDesc());
            valueEntity.setSpuId(spuInfoEntity.getId());
            return valueEntity;
        }).collect(Collectors.toList());

        // 5. 保存spu的积分信息 sms_spu_bounds
        attrValueService.saveProductAttr(collect);


        // 5. 保存当前spu对应的所有sku信息
        Bounds bounds = spuSaveVo.getBounds();
        SpuBoundTo spuBoundTo = new SpuBoundTo();
        BeanUtils.copyProperties(bounds, spuBoundTo);
        spuBoundTo.setSpuId(spuInfoEntity.getId());
            // 调用远程服务
        R r = couponFeignService.saveSpuBounds(spuBoundTo);
        if(r.getCode() != 0) {
            log.error("保存远程SPU积分信息失败");
        }


        // 5.1  sku 的基本信息  ： pms_sku_info
        List<Skus> skus = spuSaveVo.getSkus();
        if(skus != null && skus.size() > 0) {
            for (Skus item : skus) {// 获取默认图片
                String defaultImg = "";
                for (Images image : item.getImages()) {
                    if (image.getDefaultImg() == 1) {
                        defaultImg = image.getImgUrl();
                    }
                }

                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(item,skuInfoEntity);
                skuInfoEntity.setSpuId(spuInfoEntity.getId());
                //skuInfoEntity.setSkuDesc();
                skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
                skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
                skuInfoEntity.setSkuDefaultImg(defaultImg);
                skuInfoEntity.setSaleCount(0L);

                skuInfoService.saveSkuInfo(skuInfoEntity);

                // 获取sku 的 id
                Long skuId = skuInfoEntity.getSkuId();

                List<SkuImagesEntity> imagesEntities = item.getImages().stream().map(img -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setImgUrl(img.getImgUrl());
                    skuImagesEntity.setDefaultImg(img.getDefaultImg());
                    return  skuImagesEntity;
                }).filter(entity -> {
                    return StringUtils.isNotEmpty(entity.getImgUrl()); // 没有图片的路径  不给予保存 （进行过滤）
                }).collect(Collectors.toList());

                // 5.2  sku 的图片信息 ： pms_sku_image
                skuImagesService.saveBatch(imagesEntities);

                // 5.3  sku的销售属性信息 ： pms_sku_sale_attr_value
                List<Attr> attr = item.getAttr();
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attr.stream().map(a -> {
                    SkuSaleAttrValueEntity attrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(a, attrValueEntity);
                    attrValueEntity.setSkuId(skuId);
                    return attrValueEntity;
                }).collect(Collectors.toList());

                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);

                // 5.4  sku的优惠、满减信息 mall_sms -> sms_sku_ladder/sms_sku_full_reduction/sms_member_price
                SkuReductionTo skuReductionTo = new SkuReductionTo();
                BeanUtils.copyProperties(item,skuReductionTo);
                skuReductionTo.setSkuId(skuInfoEntity.getSkuId());

                //
                if(skuReductionTo.getFullCount() <=0 || skuReductionTo.getFullPrice().compareTo(new BigDecimal("0")) == 1){
                    R r1 = couponFeignService.saveSkuReduction(skuReductionTo);
                    if(r1.getCode() != 0){
                        log.error("保存远程SKU优惠信息失败");
                    }
                }
            }
        }

    }

    @Override
    public void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity) {
         this.baseMapper.insert(spuInfoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> queryWrapper = new QueryWrapper<>();

        String key = (String) params.get("key");
        if(StringUtils.isNotEmpty(key)) {
            queryWrapper.and(w -> {
                w.eq("id", key).or().like("spu_name", key);
            });
        }

        String status = (String) params.get("status");
        if(StringUtils.isNotEmpty(status)) {
            queryWrapper.eq("publish_status", status);
        }

        String brandId = (String) params.get("brandId");
        if(StringUtils.isNotEmpty(brandId) && ! "0".equalsIgnoreCase(brandId)) {
            queryWrapper.eq("brand_id", brandId);
        }

        String catelogId = (String) params.get("catelogId");
        if(StringUtils.isNotEmpty(catelogId) && ! "0".equalsIgnoreCase(catelogId)) {
            queryWrapper.eq("catalog_id", catelogId);
        }

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                queryWrapper
        );
        return new PageUtils(page);
    }

    @Override
    public void up(Long spuId) {
        // 查出当前spuid对应的所有sku信息，品牌的名字
        List<SkuInfoEntity> skus = skuInfoService.getSkusBySpuId(spuId);
        List<Long> skuIdList = skus.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());
        // TODO 4.查询当前sku的所有可以被检索的规格属性
        List<ProductAttrValueEntity> baseAttrs = attrValueService.baseAttrlistforspu(spuId);
        List<Long> attrIds = baseAttrs.stream().map(attr -> {
            return attr.getAttrId();
        }).collect(Collectors.toList());

        List<Long> searchIds = attrService.selectSearchAttrs(attrIds);
        HashSet<Long> idSet = new HashSet<>(searchIds);

        List<SkuEsModel.Attr> attrList = baseAttrs.stream().filter(item -> {
            return idSet.contains(item.getAttrId());
        }).map(item -> {
            SkuEsModel.Attr attr1 = new SkuEsModel.Attr();
            BeanUtils.copyProperties(item, attr1);
            return attr1;
        }).collect(Collectors.toList());

        // TODO 1.发送远程调用，库存系统是否有库存
        Map<Long, Boolean> stockMap = null;
        try {
            R skuHasStock = wareFeignService.getSkusHasStock(skuIdList);
            stockMap = skuHasStock.getData(new TypeReference<List<SkuHasStockVO>>(){}).stream().collect(Collectors.toMap(SkuHasStockVO::getSkuId, SkuHasStockVO::getHasStock));
        } catch (Exception e) {
            log.error("库存服务查询异常：原因{}", e);
        }

        // 封装每个sku的信息
        Map<Long, Boolean> finalStockMap = stockMap;
        List<SkuEsModel> collect = skus.stream().map(sku -> {
            // 组装需要的数据
            SkuEsModel esModel = new SkuEsModel();
            BeanUtils.copyProperties(sku, esModel);
            // skuPrice, skuImg
            esModel.setSkuPrice(sku.getPrice());
            esModel.setSkuImg(sku.getSkuDefaultImg());
            // 设置库存信息
            if(finalStockMap == null) {
                esModel.setHasStock(true);
            } else {
                esModel.setHasStock(finalStockMap.get(sku.getSkuId()));
            }
            // TODO 2.热度评分 a
            esModel.setHotScore(0L);

            // TODO 3.品牌和分类的名字信息
            BrandEntity brand = brandService.getById(esModel.getBrandId());
            esModel.setBrandName(brand.getName());
            esModel.setBrandImg(brand.getLogo());

            CategoryEntity category = categoryService.getById(esModel.getCatalogId());
            esModel.setCatalogName(category.getName());

            // 设置检索属性
            esModel.setAttrs(attrList);
            return esModel;
        }).collect(Collectors.toList());
        // TODO 5. 发给 es 进行保存 mall-search
        R r = searchFeignService.productStatusUp(collect);

        if(r.getCode() == 0) {
            // 远程调用成功
            // TODO 6.修改上架状态
            baseMapper.updateSpuStatus(spuId, ProductConstant.StatusEnum.SPU_UP.getCode());
        } else {
            // 远程调用失败
            // TODO 7.重复调用？接口幂等性？重试机制
            // 远程调用流程
            /**
             * 1.构造请求数据，将对象转为json
             * RequestTemplate template = buildTemplateFromArgs.create(argv)
             * 2.发送请求进行执行（执行成功过后会解码响应数据）
             * executeAndDecode(template)
             * 3.执行请求会有重试机制
             * while(true) {
             *     try {
             *      executeAndDecode(template);
             *     }catch () {
             *         try{
             *             ratryer.continueOrPropagate(e);
             *         } catch (Exception ex ) {
             *           throw ex;
             *           continue;
             *         }
             *     }
             * }
             */
        }

    }
}