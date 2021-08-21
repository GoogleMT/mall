package top.gumt.mall.product;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import top.gumt.mall.product.entity.BrandEntity;
import top.gumt.mall.product.service.BrandService;
import top.gumt.mall.product.service.CategoryBrandRelationService;
import top.gumt.mall.product.service.CategoryService;
import top.gumt.mall.product.vo.BrandVo;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
class MallProductApplicationTests {
    @Autowired
    BrandService brandService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Test
    void categoryTest() {
        Long[] cateLogPath = categoryService.findCateLogPath(5L);
        log.info("完整路径：{}", Arrays.asList(cateLogPath));
    }

    @Test
    void OSSTest() throws FileNotFoundException {
        // Endpoint以张家界为例，其它Region请按实际情况填写。
        String endpoint = "https://oss-cn-zhangjiakou.aliyuncs.com";
        // 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录RAM控制台创建RAM账号。
        String accessKeyId = "LTAI5tMojJ2baCmgV7QCuKMd";
        String accessKeySecret = "i0dBM55lKd6uOSy4szZDfeAK4x8Ctn";
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
// 填写本地文件的完整路径。如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件流。
        InputStream inputStream = new FileInputStream("E:\\beautifulPicture\\1.jpg");
// 依次填写Bucket名称（例如gumt-mall）和Object完整路径（例如test.jpg）。Object完整路径中不能包含Bucket名称。
        ossClient.putObject("gumt-mall", "test.jpg", inputStream);
// 关闭OSSClient。
        ossClient.shutdown();
    }

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

    @Test
    void relationBrandsList() {
        List<BrandEntity> vos = categoryBrandRelationService.getBrandsByCatId(225L);
        List<BrandVo> brandVos = vos.stream().map(item -> {
            BrandVo brandVo = new BrandVo();
            brandVo.setBrandId(item.getBrandId());
            brandVo.setBrandName(item.getName());
            return brandVo;
        }).collect(Collectors.toList());
        System.out.println(vos);
    }

}
