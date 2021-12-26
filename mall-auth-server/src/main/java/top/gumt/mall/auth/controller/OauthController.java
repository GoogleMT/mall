package top.gumt.mall.auth.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.gumt.common.constant.AuthServerConstant;
import top.gumt.common.utils.HttpUtils;
import top.gumt.common.utils.R;
import top.gumt.common.vo.MemberResponseVo;
import top.gumt.mall.auth.feign.MemberFeignService;
import top.gumt.mall.auth.vo.SocialUser;

import javax.servlet.http.HttpSession;
import java.util.HashMap;

@Controller
public class OauthController {

    @Autowired
    private MemberFeignService memberFeignService;

    @GetMapping("/oauth2.0/weibo/success")
    public String authorize (@RequestParam("code") String code, HttpSession session) throws Exception {
        // 使用code换取Access-token, 换取成功则继续， 否者重定向至登录页
        // 构造请求参数
        HashMap<String, String> query = new HashMap<>();
        query.put("client_id", "1169509120");
        query.put("client_secret", "ca7ad39ad51b477454bcb9e122957377");
        query.put("grant_type", "authorization_code");
        query.put("redirect_uri", "http://mall.com/oauth2.0/weibo/success");
        query.put("code", code);
        // 发送post请求
        HttpResponse response = HttpUtils.doPost("https://api.weibo.com",
                "/oauth2/access_token",
                "post",
                new HashMap<String, String>(),
                null,
                query);
        HashMap<String, String> errors = new HashMap<>();
        System.out.println(EntityUtils.toString(response.getEntity()));
        if (response.getStatusLine().getStatusCode() == 200) {
            // 调用member远程接口进行oauth登录，登录成功则转发至首页并携带用户信息，否则转发至登录页
            String json = EntityUtils.toString(response.getEntity());
            SocialUser socialUser = JSON.parseObject(json, new TypeReference<SocialUser>() {
            });
            // 调用远程接口
            R login = memberFeignService.login(socialUser);
            // 判断调用是否成功
            if(login.getCode() == 0) {
                String jsonString = JSON.toJSONString(login.get("memberEntity"));
                System.out.println("============" + jsonString);
                MemberResponseVo memberResponseVo = JSON.parseObject(jsonString, new TypeReference<MemberResponseVo>() {
                });
                System.out.println("============" + memberResponseVo);
                session.setAttribute(AuthServerConstant.LOGIN_USER, memberResponseVo);
                return "redirect:http://mall.com";
            } else {
                errors.put("msg", "登录失败，请重试");
                session.setAttribute("errors", errors);
                return "redirect:http://auth.mall.com/login.html";
            }
        } else {
            errors.put("msg", "获得第三方授权失败，请重试");
            session.setAttribute("errors", errors);
            return "redirect:http://auth.mall.com/login.html";
        }
    }

}
