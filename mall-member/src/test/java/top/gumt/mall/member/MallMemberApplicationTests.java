package top.gumt.mall.member;

import org.apache.commons.codec.digest.Md5Crypt;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    @Test
    void testCrypt() {
        String crypt = Md5Crypt.md5Crypt("123456789".getBytes());
        System.out.println(crypt);
        String crypt1 = Md5Crypt.md5Crypt("123456789".getBytes(), "$1$qqqqqqqq");
        System.out.println(crypt1);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        // $2a$10$n9SI72QLI7fIwD5llFCxzuJ0fqycwxS6lSM.1.xylJtXAZ9iX.EI.
        String encode = encoder.encode("123456789");
        boolean matches = encoder.matches("123456789", "$2a$10$n9SI72QLI7fIwD5llFCxzuJ0fqycwxS6lSM.1.xylJtXAZ9iX.EI.");
        System.out.println(matches);
    }

    @Test
    void testRandom() {
        System.out.println((int) ((Math.random() + 1) * 100000));
    }

}
