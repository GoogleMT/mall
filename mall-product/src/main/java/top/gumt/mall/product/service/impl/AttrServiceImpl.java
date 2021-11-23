package top.gumt.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import top.gumt.common.constant.ProductConstant;
import top.gumt.common.utils.PageUtils;
import top.gumt.common.utils.Query;

import top.gumt.mall.product.dao.AttrAttrgroupRelationDao;
import top.gumt.mall.product.dao.AttrDao;
import top.gumt.mall.product.dao.AttrGroupDao;
import top.gumt.mall.product.dao.CategoryDao;
import top.gumt.mall.product.entity.AttrAttrgroupRelationEntity;
import top.gumt.mall.product.entity.AttrEntity;
import top.gumt.mall.product.entity.AttrGroupEntity;
import top.gumt.mall.product.entity.CategoryEntity;
import top.gumt.mall.product.service.AttrService;
import top.gumt.mall.product.service.CategoryService;
import top.gumt.mall.product.vo.AttrGroupRelationVo;
import top.gumt.mall.product.vo.AttrResponseVo;
import top.gumt.mall.product.vo.AttrVo;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    AttrAttrgroupRelationDao relationDao;

    @Autowired
    AttrGroupDao attrGroupDao;
    @Autowired
    CategoryDao categoryDao;

    @Autowired
    CategoryService categoryService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveAttr(AttrVo attrVo) {
        AttrEntity attrEntity = new AttrEntity();
        // 使用BeanUtils属性拷贝
        BeanUtils.copyProperties(attrVo, attrEntity);
        // 保存基本数据
        this.save(attrEntity);
        if(ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode() == attrEntity.getAttrType() && attrVo.getAttrGroupId() != null ){
            //保存关联关系
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrId(attrEntity.getAttrId());
            relationEntity.setAttrGroupId(attrVo.getAttrGroupId());
            relationDao.insert(relationEntity);
        }
    }

    @Override
    public PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String type) {
        // 传入的attrType是"base"或其他，但是数据库存的是 属性类型[0-销售属性，1-基本属性]
        // 属性都在pms_attr表中混合着
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<AttrEntity>()
                .eq("attr_type","base".equalsIgnoreCase(type)?
                ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode():
                ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());

        // 如果参数带有分类id，则按分类查询
        if (catelogId != 0L ) {
            wrapper.eq("catelog_id", catelogId);
        }
        // 支持模糊查询，用id或者name查
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.and((w) -> {
                w.eq("attr_id", key).or().like("attr_name", key);
            });
        }
        // 正式查询满足条件的属性
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                wrapper
        );
        List<AttrEntity> records = page.getRecords();
        PageUtils pageUtils = new PageUtils(page);

        // 查到属性后还要结合分类名字、分组名字(分类->属性->分组) 封装为AttrRespVo对象
        List<AttrResponseVo> attrRespVos = records.stream().map((attrEntity) -> {
            AttrResponseVo attrRespVo = new AttrResponseVo();
            BeanUtils.copyProperties(attrEntity, attrRespVo);

            // 1.设置分类和分组的名字  先获取中间表对象  给attrRespVo 封装分组名字
            if("base".equalsIgnoreCase(type)){ // 如果是规格参数才查询，或者说销售属性没有属性分组，只有分类
                // 根据属性id查询关联表，得到其属性分组
                AttrAttrgroupRelationEntity entity = relationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));
                if (entity != null && entity.getAttrGroupId() != null) {
                    AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(entity);
                    // 设置属性分组的名字
                    attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }

            // 2.查询分类id 给attrRespVo 封装三级分类名字
            CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
            if (categoryEntity != null) {
                attrRespVo.setCatelogName(categoryEntity.getName());
            }
            return attrRespVo;
        }).collect(Collectors.toList());
        pageUtils.setList(attrRespVos);
        return pageUtils;
    }

    @Override
    public AttrResponseVo getAttrInfo(Long attrId) {
        AttrEntity byId = this.getById(attrId);
        AttrResponseVo attrRespVo = new AttrResponseVo();
        BeanUtils.copyProperties(byId, attrRespVo);

        AttrAttrgroupRelationEntity relationEntity = relationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", byId.getAttrId()));

        if(ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode() == byId.getAttrType()){
            //1. 设置分组信息
            if (null != relationEntity) {
                Long attrGroupId = relationEntity.getAttrGroupId();
                attrRespVo.setAttrGroupId(attrGroupId);
                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrGroupId);
                if (null != attrGroupEntity) {
                    attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }
        }

        //2. 设置分类信息
        Long catelogId = attrRespVo.getCatelogId();
        Long[] catelogPath = categoryService.findCateLogPath(catelogId);
        attrRespVo.setCatelogPath(catelogPath);
        CategoryEntity categoryEntity = categoryDao.selectById(catelogId);
        if (null != categoryEntity) {
            attrRespVo.setCatelogName(categoryEntity.getName());
        }
        return attrRespVo;
    }

    @Transactional
    @Override
    public void updateAttr(AttrVo attrVo) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attrVo, attrEntity);
        this.updateById(attrEntity);

        if(ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode() == attrEntity.getAttrType()){
            //修改关联分组
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrId(attrVo.getAttrId());
            relationEntity.setAttrGroupId(attrVo.getAttrGroupId());
            UpdateWrapper<AttrAttrgroupRelationEntity> updateWrapper = new UpdateWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrVo.getAttrId());

            Integer count = relationDao.selectCount(updateWrapper);
            if( count > 0){
                relationDao.update(relationEntity,updateWrapper);
            }else {
                relationDao.insert(relationEntity);
            }
        }
    }

    /**
     * 根据分组id关联所有的属性
     * @param attrgroupId
     * @return
     */
    @Override
    public List<AttrEntity> getRelationAttr(Long attrgroupId) {
        QueryWrapper<AttrAttrgroupRelationEntity> queryWrapper = new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrgroupId);
        List<AttrAttrgroupRelationEntity> relationEntities = relationDao.selectList(queryWrapper);
        List<AttrEntity> entityList = relationEntities.stream().map(attrAttrgroupRelationEntity -> {
            Long attrId = attrAttrgroupRelationEntity.getAttrId();

            return this.getById(attrId);
        }).filter(attrEntity -> {
            return attrEntity != null;
        }).collect(Collectors.toList());

        return entityList;
    }

    @Override
    public void deleteRelation(AttrGroupRelationVo[] attrGroupRelationVos) {
        List<AttrAttrgroupRelationEntity> entityList = Arrays.asList(attrGroupRelationVos).stream().map(param -> {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(param, relationEntity);
            return relationEntity;
        }).collect(Collectors.toList());
        relationDao.deleteBatchRelation(entityList);
    }

    @Override
    public PageUtils getNoRelation(Map<String, Object> params, Long attrgroupId) {
        //1. 当前分组只能关联自己所属的分类的所有属性
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupId);
        Long catelogId = attrGroupEntity.getCatelogId();
        //2. 当前分组只能关联别的分组没有引用的属性
        //2.1)、当前分类下的其他属性分组
        List<AttrGroupEntity> groupEntities = attrGroupDao.selectList(new QueryWrapper<AttrGroupEntity>()
                .eq("catelog_id", catelogId));
//                .ne("attr_group_id", attrgroupId));
        List<Long> collect = groupEntities.stream().map(item -> {
            return item.getAttrGroupId();
        }).collect(Collectors.toList());
        //2.2)、这些分组关联的属性
        List<AttrAttrgroupRelationEntity> entities = relationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().in("attr_group_id",collect));
        List<Long> attrIds = entities.stream().map(item -> {
            return item.getAttrId();
        }).collect(Collectors.toList());
        //2.3)、从当前分类的所有属性中移除这些属性
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<AttrEntity>().eq("catelog_id", catelogId).eq("attr_type",ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode());

        if(null != attrIds && attrIds.size() > 0){
            queryWrapper.notIn("attr_id", attrIds);
        }

        String key = (String)params.get("key");
        if(StringUtils.isNotEmpty(key)){
            queryWrapper.and(w ->{
                w.eq("attr_id",key).or().like("attr_name",key);
            });
        }

        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), queryWrapper);

        PageUtils pageUtils = new PageUtils(page);


        return pageUtils;
    }

    @Override
    public List<Long> selectSearchAttrs(List<Long> attrIds) {
        return baseMapper.selectSearchAttrs(attrIds);
    }
}