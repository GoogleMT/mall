package top.gumt.mall.malltestssoserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
public class LoginController {

    @Autowired
    StringRedisTemplate redisTemplate;

    @GetMapping("/login.html")
    public String loginPage(@RequestParam("redirect_url") String redirectUrl,
                            Model model,
                            @CookieValue(value = "sso_token", required = false) String ssoToken) {
        if(!StringUtils.isEmpty(ssoToken)) {
            // 说明之前有人登录过了
            return "redirect:" + redirectUrl + "?token=" + ssoToken;
        }


        model.addAttribute("redirectUrl", redirectUrl);
        return "login";
    }

    @PostMapping("/doLogin")
    public String doLogin (String username, String password, String url, HttpServletResponse response) {

        if(!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {
            // 登录成功跳回之前的页面

            String uuid = UUID.randomUUID().toString().replace("-", "");
            redisTemplate.opsForValue().set(uuid, username);

            //
            Cookie ssoToken = new Cookie("sso_token", uuid);
            response.addCookie(ssoToken);

            return "redirect:" + url + "?token=" + uuid;
        } else {
            // 失败继续进行登录
            return "login";
        }
    }
}
