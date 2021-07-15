package top.gumt.mall.member;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.gumt.mall.member.service.MemberService;

@SpringBootTest
class MallMemberApplicationTests {

    @Autowired
    MemberService memberService;

    @Test
    void contextLoads() {
        int count = memberService.count();
        System.out.println(count);
    }

}
