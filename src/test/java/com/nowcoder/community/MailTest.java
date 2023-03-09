package com.nowcoder.community;

import com.nowcoder.community.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTest {

    @Autowired
    private MailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;
    @Test
    public void TestMail(){
        mailClient.sendEmail("h1596252279@163.com","text","aertygfdsasefrg");
    }
    @Test
    public void TestMail1(){
        Context context = new Context();
        context.setVariable("username","suhhdf");

        String process = templateEngine.process("/mail/demo", context);
        System.out.println(process);
        mailClient.sendEmail("h1596252279@163.com","text",process);
    }
}
