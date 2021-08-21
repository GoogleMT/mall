package top.gumt.mall.thirdparty;

import com.aliyun.oss.OSSClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

@SpringBootTest
class MallThirdPartyApplicationTests {
    @Autowired
    OSSClient ossClient;


    @Test
    void contextLoads() throws FileNotFoundException {
        FileInputStream inputStream = new FileInputStream("E:\\beautifulPicture\\2.jpg");
        // 参数1位bucket  参数2位最终名字
        ossClient.putObject("gumt-mall", "32145.jpg", inputStream);
        ossClient.shutdown();
    }

}
