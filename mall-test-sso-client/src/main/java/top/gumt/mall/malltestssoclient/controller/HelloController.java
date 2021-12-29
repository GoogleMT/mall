package top.gumt.mall.malltestssoclient.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;

@Controller
public class HelloController {

    @Value("${sso.server.url}")
    private String ssoServerUrl;

    @ResponseBody
    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    @GetMapping("/employees")
    public String employees(Model model,
                            HttpSession session,
                            @RequestParam(value = "token",required = false) String token) {
        if(!StringUtils.isEmpty(token)) {
            // TODO 去ssoserver获取当前用户真正对应的用户信息
            session.setAttribute("loginUser", "zhangsan");
        }

        // session里有loginUser就代表登录过了，
        Object loginUser = session.getAttribute("loginUser");
        if(loginUser == null) {
            // 如果在session中没有loginUser
            return "redirect:" + ssoServerUrl + "?redirect_url=http://client1.com:8081/employees";
        } else {
            ArrayList<String> employees = new ArrayList<>();
            employees.add("张三");
            employees.add("李四");
            model.addAttribute("emps", employees);
            return "list";
        }
    }
}
