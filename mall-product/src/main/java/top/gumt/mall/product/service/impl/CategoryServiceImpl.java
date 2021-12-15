package top.gumt.mall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.lang.reflect.Executable;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import top.gumt.common.utils.PageUtils;
import top.gumt.common.utils.Query;

import top.gumt.mall.product.dao.CategoryDao;
import top.gumt.mall.product.entity.CategoryEntity;
import top.gumt.mall.product.service.CategoryBrandRelationService;
import top.gumt.mall.product.service.CategoryService;
import top.gumt.mall.product.vo.Catalog3List;
import top.gumt.mall.product.vo.Catelog2Vo;

@Slf4j
@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {
    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    RedissonClient redissonClient;

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
        List<Long> paths = new ArrayList<>();

        List<Long> parentPaths = findParentPath(catelogId, paths);
        // 数组反转
        Collections.reverse(parentPaths);
        // 返回 1级 2级 3级
        return parentPaths.toArray(new Long[parentPaths.size()]);
    }

    /**
     * 级联更新所有关联的数据
     */
    @CacheEvict(value = {"category"}, allEntries = true)
    @Caching(evict = {
            @CacheEvict(value = {"category"}, key = "'getLevel1Categories'"),
            @CacheEvict(value = {"category"}, key = "'getCatelogJson'")
    })
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateCascade(CategoryEntity categoryEntity) {
        this.updateById(categoryEntity);
        categoryBrandRelationService.updateCategory(categoryEntity.getCatId(), categoryEntity.getName());
    }

    public List<Long> findParentPath(Long catelogId, List<Long> paths) {
        // 1.收集当前几点id  如： 父子孙层级 返回 孙 子 父
        paths.add(catelogId);
        CategoryEntity id = this.getById(catelogId);
        if (id.getParentCid() != 0) {
            // 进行递归
            findParentPath(id.getParentCid(), paths);
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
    // 每一个需要缓存的数据都指定要存放的key即名字【缓存分区按照业务类型分】
    // 代表当前方法的结果需要缓存，如果缓存有，则方法不调用。如果缓存中没有，会调用方法，最后将方法的结果放入到缓存中+
    // value相当于cacheNames  key如果是字符串只能使用"''"
    // sync表示改方法的缓存被读取时会加锁
    @Cacheable(value = {"category"}, key = "#root.methodName", sync = true)
    @Override
    public List<CategoryEntity> getLevel1Categories() {
        log.info("查询一级分类数据");
        //找出一级分类
        List<CategoryEntity> categoryEntities = this.baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("cat_level", 1));
        return categoryEntities;
    }

    @Cacheable(value = {"category"}, key = "#root.methodName", sync = true)
    @Override
    public Map<String, List<Catelog2Vo>> getCatelogJson() {
        /**
         * 将数据库的多次查询变为一次
         */
        List<CategoryEntity> selectList = baseMapper.selectList(null);
        // 1.查出所有一级分类
        List<CategoryEntity> parentCid = getParentCid(selectList, 0L);

        // 2.封装数据
        Map<String, List<Catelog2Vo>> parent_cid = parentCid.stream().collect(Collectors.toMap(
                k -> k.getCatId().toString(),
                v -> {
                    // 每一个的一级分类，查到这个一级分类的二级分类
                    List<CategoryEntity> categoryEntities = getParentCid(selectList, v.getCatId());
                    // 封装上面的结果
                    List<Catelog2Vo> catelog2Vos = null;
                    if (categoryEntities != null) {
                        catelog2Vos = categoryEntities.stream().map(level2 -> {
                            Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, level2.getCatId().toString(), level2.getName());
                            // 当前二级分类的三级分类
                            List<CategoryEntity> level3Catelog = getParentCid(selectList, level2.getCatId());
                            if (level3Catelog != null) {
                                List<Catalog3List> collect = level3Catelog.stream().map(level3 -> {
                                    // 封装指定格式
                                    Catalog3List catalog3List = new Catalog3List(level2.getCatId().toString(), level3.getCatId().toString(), level3.getName());
                                    return catalog3List;
                                }).collect(Collectors.toList());
                                catelog2Vo.setCatalog3List(collect);
                            }
                            return catelog2Vo;
                        }).collect(Collectors.toList());
                    }
                    return catelog2Vos;
                }));
        return parent_cid;
    }

    //    @Override
    public Map<String, List<Catelog2Vo>> getCatelogJson2() {
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        // 从缓存中获取
        String catelogJson = ops.get("catelogJson");
        if(catelogJson == null) {
            // 缓存中没有数据， 查询数据库
            Map<String, List<Catelog2Vo>> catelogJsonFromDb = getCatelogJsonFromDbWithRedisLock();

            return catelogJsonFromDb;
        }
        System.out.println("缓存命中直接返回.....");
        Map<String, List<Catelog2Vo>> listMap = JSON.parseObject(catelogJson, new TypeReference<Map<String, List<Catelog2Vo>>>() {
        });
        return listMap;
    }


    // 从数据库中查询并封装分类数据
    public Map<String, List<Catelog2Vo>> getCatelogJsonFromDbWithRedisLock() {
        Map<String, List<Catelog2Vo>> dataFromDb;
        RLock lock = redissonClient.getLock("CatelogJson-lock");
        lock.lock();
        try {
            dataFromDb = getDataFromDb();
        } finally {
            lock.unlock();
        }
        return dataFromDb;
    }

    private Map<String, List<Catelog2Vo>> getDataFromDb() {
        // 再去缓存中确认一次
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        String catelogJson = ops.get("catelogJson");
        if (!StringUtils.isEmpty(catelogJson)) {
            Map<String, List<Catelog2Vo>> listMap = JSON.parseObject(catelogJson, new TypeReference<Map<String, List<Catelog2Vo>>>() {
            });
            return listMap;
        }
        System.out.println("查询了数据库......");
        // 从缓存中获取
        /**
         * 将数据库的多次查询变为一次
         */
        List<CategoryEntity> selectList = baseMapper.selectList(null);
        // 1.查出所有一级分类
        List<CategoryEntity> parentCid = getParentCid(selectList, 0L);

        // 2.封装数据
        Map<String, List<Catelog2Vo>> parent_cid = parentCid.stream().collect(Collectors.toMap(
                k -> k.getCatId().toString(),
                v -> {
                    // 每一个的一级分类，查到这个一级分类的二级分类
                    List<CategoryEntity> categoryEntities = getParentCid(selectList, v.getCatId());
                    // 封装上面的结果
                    List<Catelog2Vo> catelog2Vos = null;
                    if (categoryEntities != null) {
                        catelog2Vos = categoryEntities.stream().map(level2 -> {
                            Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, level2.getCatId().toString(), level2.getName());
                            // 当前二级分类的三级分类
                            List<CategoryEntity> level3Catelog = getParentCid(selectList, level2.getCatId());
                            if (level3Catelog != null) {
                                List<Catalog3List> collect = level3Catelog.stream().map(level3 -> {
                                    // 封装指定格式
                                    Catalog3List catalog3List = new Catalog3List(level2.getCatId().toString(), level3.getCatId().toString(), level3.getName());
                                    return catalog3List;
                                }).collect(Collectors.toList());
                                catelog2Vo.setCatalog3List(collect);
                            }
                            return catelog2Vo;
                        }).collect(Collectors.toList());
                    }
                    return catelog2Vos;
                }));
        // 将查询到的数据放入到缓存中
        String toJSONString = JSON.toJSONString(parent_cid);
        ops.set("catelogJson", toJSONString, 1, TimeUnit.DAYS);
        return parent_cid;
    }

    // 从数据库中查询并封装分类数据
    public Map<String, List<Catelog2Vo>> getCatelogJsonFromDbWithLocalLock() {
        synchronized (this) {
            return getDataFromDb();
        }
    }

    /**
     * 在selectList中找到parentId等于传入的parentCid的所有分类数据
     * @param selectList
     * @param parentCid
     * @return
     */
    private List<CategoryEntity> getParentCid(List<CategoryEntity> selectList,Long parentCid) {
        List<CategoryEntity> collect = selectList.stream().filter(item -> Objects.equals(item.getParentCid(), parentCid)).collect(Collectors.toList());
        return  collect;
    }
}