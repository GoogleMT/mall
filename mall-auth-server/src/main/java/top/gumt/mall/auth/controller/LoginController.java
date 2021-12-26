package top.gumt.mall.auth.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import top.gumt.common.constant.AuthServerConstant;
import top.gumt.common.exception.BizCodeEnum;
import top.gumt.common.utils.R;
import top.gumt.common.vo.MemberResponseVo;
import top.gumt.mall.auth.feign.MemberFeignService;
import top.gumt.mall.auth.feign.ThirdPartFeignService;
import top.gumt.mall.auth.vo.UserLoginVo;
import top.gumt.mall.auth.vo.UserRegisterVo;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Controller
public class LoginController {

    @Autowired
    ThirdPartFeignService thirdPartFeignService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    MemberFeignService memberFeignService;

    @GetMapping("/login.html")
    public String login(HttpSession session) {
        // 从会话中获取loginUser
        Object attribute = session.getAttribute(AuthServerConstant.LOGIN_USER);
        System.out.println("attribute：" + attribute);
        if (attribute == null) {
            return "login";
        }
        System.out.println("已经登录过了，重定向到首页");
        return "redirect:http://mall.com/";
    }

    @RequestMapping("/login")
    public String login(UserLoginVo vo,
                        RedirectAttributes attributes,
                        HttpSession session){
        // 调用远程登录
        R r = memberFeignService.login(vo);
        if (r.getCode() == 0) {
            String jsonString = JSON.toJSONString(r.get("memberEntity"));
            MemberResponseVo memberResponseVo = JSON.parseObject(jsonString, new TypeReference<MemberResponseVo>() {
            });
            session.setAttribute(AuthServerConstant.LOGIN_USER, memberResponseVo);
            log.info("\n欢迎[" + memberResponseVo.getUsername() +"]登录");
            // 登录成功重定向首页
            return "redirect:http://mall.com/";
        } else {
            String msg = (String) r.get("msg");
            Map<String, String> errors = new HashMap<>();
            errors.put("msg", msg);
            attributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.mall.com/login.html";
        }
    }


    @GetMapping("reg.html")
    public String register() {
        return "reg";
    }

    @ResponseBody
    @GetMapping("/sms/sendCode")
    public R sendCode(@RequestParam("phone") String phone) {
        // 接口防刷，在redis中缓存phone-code
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        String prePhone = AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone;
        String v = ops.get(prePhone);
        // 检验是否为空
        if (StringUtils.isNotEmpty(v)){
            long pre = Long.parseLong(v.split("_")[1]);
            // 如果存储的是按小于60s 说明60s内发送过验证码
            if(System.currentTimeMillis() - pre < 60000 ) {
                return R.error(BizCodeEnum.SMS_CODE_EXCEPTION.getCode(), BizCodeEnum.SMS_CODE_EXCEPTION.getMsg());
            }
        }
        // 如果存在的话，删除之前的验证码
        stringRedisTemplate.delete(prePhone);
        // 获取到6位数字的验证码
        String code = String.valueOf((int) ((Math.random() + 1) * 100000));
        // redis 中进行存储并设置过期时间 十分钟有效
        ops.set(prePhone, code + "_" + System.currentTimeMillis(), 10, TimeUnit.MINUTES);
        // 发送验证码
        thirdPartFeignService.sendCode(phone, code);
        return R.ok();
    }

    @PostMapping("/register")
    public String register(@Valid UserRegisterVo registerVo, BindingResult result, RedirectAttributes attributes) {
        // 判断校验是否通过
        HashMap<String, String> errors = new HashMap<>();
        if(result.hasErrors()) {
            // 如果校验通过，则封装校验结果
            result.getFieldErrors().forEach(item -> {
                errors.put(item.getField(), item.getDefaultMessage());
                // 将错误信息封装到session中
                attributes.addFlashAttribute("errors", errors);
            });
            // 重定向到注册页
            return "redirect:http://auth.mall.com/reg.html";
        } else {
            // 如果JSR303校验通过
            // 判断验证码是否正确
            String code = stringRedisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + registerVo.getPhone());
            // 如果对应的验证码不为空且与提交上的相等 =》 验证码正确
            if(!StringUtils.isEmpty(code) &&  registerVo.getCode().equals(code.split("_")[0])) {
                // 使得验证后的验证码失效
                stringRedisTemplate.delete(AuthServerConstant.SMS_CODE_CACHE_PREFIX + registerVo.getPhone());
                // 调用远程会员服务注册
                R r = memberFeignService.register(registerVo);
                if (r.getCode() == 0) {
                    // 如果成功，重定向登录页
                    return "redirect:http://auth.mall.com/login.html";
                } else {
                    // 调用失败，返回注册页面并显示错误信息
                    String msg = (String) r.get("msg");
                    errors.put("msg", msg);
                    attributes.addFlashAttribute("errors", errors);
                    return "redirect:http://auth.mall.com/reg.html";
                }
            } else {
                // 验证码错误
                errors.put("code", "验证码错误");
                attributes.addFlashAttribute("errors", errors);
                return "redirect:http://auth.mall.com/reg.html";
            }
        }
    }



}
