package top.gumt.mall.product.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.gumt.common.utils.PageUtils;
import top.gumt.common.utils.Query;

import top.gumt.mall.product.dao.AttrGroupDao;
import top.gumt.mall.product.entity.AttrEntity;
import top.gumt.mall.product.entity.AttrGroupEntity;
import top.gumt.mall.product.service.AttrGroupService;
import top.gumt.mall.product.service.AttrService;
import top.gumt.mall.product.vo.AttrGroupWithAttrsVo;
import top.gumt.mall.product.vo.SpuItemAttrGroupVo;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    AttrService attrService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }
    //根据分类返回属性分组 AttrGroupServiceImpl.java 按关键字或id查询
    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {
        String queryKey = (String) params.get("key");
        QueryWrapper<AttrGroupEntity> queryWrapper = new QueryWrapper<AttrGroupEntity>();
        //catelog_id == 0时，按照attr_group_id和attr_group_name进行模糊查询，否则要带上catelog_id
        if(catelogId != 0){
            queryWrapper.eq("catelog_id", catelogId);
        }
        //select * from pms_attr_group WHERE catelog_id = 1 AND (attr_group_id =key or attr_group_name LIKE '%key%');
        if (StringUtils.isNotEmpty(queryKey)) {
            queryWrapper.and((param) -> {
                param.eq("attr_group_id", queryKey)
                        .or()
                        .like("attr_group_name", queryKey);
            });
        }
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                queryWrapper
        );
        return new PageUtils(page);
    }

    @Override
    public List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCatelogId(Long catelogId) {
        //查询分类下所关联的所有分组信息
        List<AttrGroupEntity> attrGroupEntities = baseMapper.selectList(new QueryWrapper<AttrGroupEntity>()
                .eq("catelog_id", catelogId));
        //根据分组信息，得到所有的属性信息
        List<AttrGroupWithAttrsVo> attrGroupWithAttrVos = attrGroupEntities.stream().map(item -> {
            AttrGroupWithAttrsVo attrGroupWithAttrVo = new AttrGroupWithAttrsVo();
            BeanUtils.copyProperties(item, attrGroupWithAttrVo);
            List<AttrEntity> relationAtr = attrService.getRelationAttr(attrGroupWithAttrVo.getAttrGroupId());
            attrGroupWithAttrVo.setAttrs(relationAtr);
            return attrGroupWithAttrVo;
        }).collect(Collectors.toList());


        return attrGroupWithAttrVos;
    }

    @Override
    public List<SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuId(Long spuId, Long catalogId) {
        // 1.查询出当前SPU对应的所有属性的分组信息，以及当前分组下的所有属性对应的值
        return this.baseMapper.getAttrGroupWithAttrsBySpuId(spuId,catalogId);
    }
}