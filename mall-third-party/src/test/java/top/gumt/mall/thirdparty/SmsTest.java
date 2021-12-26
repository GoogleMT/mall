package top.gumt.mall.thirdparty;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.gumt.mall.thirdparty.component.SmsComponent;

import javax.annotation.Resource;

@SpringBootTest
class SmsTest {

    @Autowired
    private SmsComponent smsComponent;

    @Test
    void testSendCode () {
        try {
            smsComponent.sendCode("13886770053", "981008");
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
