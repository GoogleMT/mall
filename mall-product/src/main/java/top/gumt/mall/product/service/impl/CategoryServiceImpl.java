package top.gumt.mall.product.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.gumt.common.utils.PageUtils;
import top.gumt.common.utils.Query;

import top.gumt.mall.product.dao.CategoryDao;
import top.gumt.mall.product.entity.CategoryEntity;
import top.gumt.mall.product.service.CategoryBrandRelationService;
import top.gumt.mall.product.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {
    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        // 1.查出所有的分类
        List<CategoryEntity> entities = baseMapper.selectList(null);
        // 2. 组装成tree
        // 2.1 找出所有的以及分类
        List<CategoryEntity> level1Menus = entities.stream().filter(categoryEntity ->
            categoryEntity.getParentCid() == 0
        ).map((menu) -> {
            menu.setChildren(getChildrens(menu, entities));
            return menu;
        }).sorted((menu1, menu2) -> {
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());
        return level1Menus;
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        // TODO 1.检查当前删除的菜单，是否被别的地方引用
        // 2. 使用逻辑删除
        this.removeByIds(asList);
    }

    @Override
    public Long[] findCateLogPath(Long catelogId) {
        List<Long> paths=new ArrayList<>();

        List<Long> parentPaths = findParentPath(catelogId, paths);
        // 数组反转
        Collections.reverse(parentPaths);
        // 返回 1级 2级 3级
        return parentPaths.toArray(new Long[parentPaths.size()]);
    }

    /**
     * 级联更新所有关联的数据
     */
    @Override
    public void updateCascade(CategoryEntity categoryEntity) {
        this.updateById(categoryEntity);
        categoryBrandRelationService.updateCategory(categoryEntity.getCatId(), categoryEntity.getName());
    }

    public List<Long> findParentPath(Long catelogId, List<Long> paths){
        // 1.收集当前几点id  如： 父子孙层级 返回 孙 子 父
        paths.add(catelogId);
        CategoryEntity id = this.getById(catelogId);
        if(id.getParentCid() != 0){
            // 进行递归
            findParentPath(id.getParentCid(),paths);
        }
        return paths;
    }


    private List<CategoryEntity> getChildrens(CategoryEntity root, List<CategoryEntity> all) {
        List<CategoryEntity> children = all.stream()
                .filter(categoryEntity -> {
                    return categoryEntity.getParentCid() == root.getCatId();
                }).map(categoryEntity -> {
                    categoryEntity.setChildren(getChildrens(categoryEntity, all));
                    return categoryEntity;
                }).sorted((menu1, menu2) -> {
                    return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
                }).collect(Collectors.toList());
        return children;
    }

}