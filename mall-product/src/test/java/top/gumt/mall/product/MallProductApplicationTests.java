package top.gumt.mall.product;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import top.gumt.mall.product.entity.BrandEntity;
import top.gumt.mall.product.service.BrandService;

@RunWith(SpringRunner.class)
@SpringBootTest
class MallProductApplicationTests {
    @Autowired
    BrandService brandService;

    @Test
    void contextLoads() {
        BrandEntity brandEntity = new BrandEntity();
        brandEntity.setDescript("测试添加品牌");
        brandEntity.setName("华为");
        brandService.save(brandEntity);
        System.out.println("品牌添加成功");
    }

    @Test
    void testUpdateBrand() {
        BrandEntity brandEntity = new BrandEntity();
        brandEntity.setBrandId(1L);
        brandEntity.setDescript("测试修改品牌数据");
        brandService.updateById(brandEntity);
    }

}
