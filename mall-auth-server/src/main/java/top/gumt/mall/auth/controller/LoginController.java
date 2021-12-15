package top.gumt.mall.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login.html")
    public String login() {
        return "login";
    }
    
    @GetMapping("reg.html")
    public String register() {
        return "reg";
    }
}
