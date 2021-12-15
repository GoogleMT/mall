package top.gumt.mall.product.app;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import top.gumt.common.valid.AddGroup;
import top.gumt.common.valid.UpdateGroup;
import top.gumt.common.valid.UpdateStatusGroup;
import top.gumt.mall.product.entity.BrandEntity;
import top.gumt.mall.product.service.BrandService;
import top.gumt.common.utils.PageUtils;
import top.gumt.common.utils.R;


/**
 * 品牌
 *
 * @author zhaoming
 * @email gumt0310@gmail.com
 * @date 2021-07-15 20:17:48
 */
@RestController
@RequestMapping("product/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:brand:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = brandService.queryPage(params);

        return R.ok().put("page", page);
    }



    /**
     * 信息
     */
    @RequestMapping("/info/{brandId}")
    // @RequiresPermissions("product:brand:info")
    public R info(@PathVariable("brandId") Long brandId){
		BrandEntity brand = brandService.getById(brandId);

        return R.ok().put("brand", brand);
    }
    
    @GetMapping("/infos")
    public R infos(@RequestParam("brandIds") List<Long> barndId) {
        List<BrandEntity> brands = brandService.getBrandsByIds(barndId);
        return R.ok().put("brand", brands);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:brand:save")
    public R save(@Validated(AddGroup.class) @RequestBody BrandEntity brand/*, BindingResult result*/){
        // 手动异常处理
//        if( result.hasErrors()){
//            Map<String,String> map=new HashMap<>();
//            //1.获取错误的校验结果
//            result.getFieldErrors().forEach((item)->{
//                //获取发生错误时的message
//                String message = item.getDefaultMessage();
//                //获取发生错误的字段
//                String field = item.getField();
//                map.put(field,message);
//            });
//            return R.error(400,"提交的数据不合法").put("data",map);
//        }
        brandService.save(brand);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:brand:update")
    public R update(@Validated(UpdateGroup.class) @RequestBody BrandEntity brand){
        brandService.updateDetails(brand);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update/status")
    //@RequiresPermissions("product:brand:update")
    public R updateStatus(@Validated(UpdateStatusGroup.class) @RequestBody BrandEntity brand){
        brandService.updateById(brand);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:brand:delete")
    public R delete(@RequestBody Long[] brandIds){
		brandService.removeByIds(Arrays.asList(brandIds));

        return R.ok();
    }

}
