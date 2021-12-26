package top.gumt.mall.thirdparty.controller;

import lombok.SneakyThrows;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import top.gumt.common.utils.R;
import top.gumt.mall.thirdparty.component.SmsComponent;

import javax.annotation.Resource;

@Controller
@RequestMapping(value = "/sms")
public class SmsSendController {

    @Resource
    SmsComponent smsComponent;

    /**
     * 提供给别的服务进行调用
     * @param phone
     * @param code
     * @return
     */
    @SneakyThrows
    @ResponseBody
    @RequestMapping(value = "/sendCode")
    public R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code) {
        //发送验证码
        smsComponent.sendCode(phone,code);
        System.out.println(phone+code);
        return R.ok();
    }

}
