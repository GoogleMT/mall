package top.gumt.mall.product.app;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import top.gumt.mall.product.entity.AttrEntity;
import top.gumt.mall.product.entity.AttrGroupEntity;
import top.gumt.mall.product.service.AttrAttrgroupRelationService;
import top.gumt.mall.product.service.AttrGroupService;
import top.gumt.common.utils.PageUtils;
import top.gumt.common.utils.R;
import top.gumt.mall.product.service.AttrService;
import top.gumt.mall.product.service.CategoryService;
import top.gumt.mall.product.vo.AttrGroupRelationVo;
import top.gumt.mall.product.vo.AttrGroupWithAttrsVo;


/**
 * 属性分组
 *
 * @author zhaoming
 * @email gumt0310@gmail.com
 * @date 2021-07-15 20:17:48
 */
@Slf4j
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    AttrService attrService;

    @Autowired
    AttrAttrgroupRelationService relationService;

    @GetMapping("/{catelogId}/withattr")
    public R addRelation(@PathVariable("catelogId")Long catelogId) {
        //1.查出当前分类下的所有属性分组
        //2.查出每个属性分组的所有属性
        List<AttrGroupWithAttrsVo> vos = attrGroupService.getAttrGroupWithAttrsByCatelogId(catelogId);
        return R.ok().put("data", vos);
    }

    @GetMapping("/{attrgroupId}/attr/relation")
    public R attrRelation(@PathVariable("attrgroupId") Long attrgroupId) {
        List<AttrEntity> entities = attrService.getRelationAttr(attrgroupId);
        return R.ok().put("data", entities);
    }

    @GetMapping("/{attrgroupId}/noattr/relation")
    public R attrNoRelation(@PathVariable("attrgroupId") Long attrgroupId,
                            @RequestParam Map<String, Object> params) {
        PageUtils page = attrService.getNoRelation(params, attrgroupId);
        return R.ok().put("page", page);
    }

    @PostMapping("/attr/relation/delete")
    public R deleteRealtion(@RequestBody AttrGroupRelationVo[] attrGroupRelationVos) {
        attrService.deleteRelation(attrGroupRelationVos);
        return R.ok();
    }

    @PostMapping("/attr/relation")
    public R addRelation(@RequestBody List<AttrGroupRelationVo> relationVos) {
        relationService.saveAttrRelations(relationVos);
        return R.ok();
    }

    /**
     * 列表
     * @param  catelogId 0的话查所有
     */
    @RequestMapping("/list/{catelogId}")
    public R list(@RequestParam Map<String, Object> params,
                  @PathVariable Long catelogId){
        //        PageUtils page = attrGroupService.queryPage(params);
        PageUtils page = attrGroupService.queryPage(params,catelogId);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    //@RequiresPermissions("product:attrgroup:info")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
        // 用当前分类id查询完整路径并写入到 attrGroup
        Long[] paths = categoryService.findCateLogPath(attrGroup.getCatelogId());
        attrGroup.setCatelogPath(paths);
        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attrgroup:save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }


    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attrgroup:update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attrgroup:delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

}
