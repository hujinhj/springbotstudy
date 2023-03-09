package com.nowcoder.community.service;

import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextpath;

    public User findUserById(int id) {
        return userMapper.selectById(id);
    }

    public Map<String,Object> register(User user)  {
        Map<String,Object> map=new HashMap<>();
        if(user==null){
            try {
                throw  new IllegalAccessException("不能为空");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        if(StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg","账号不能为空");
            return map;
        }
        if(StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg","账号不能为空");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg","账号不能为空");
            return map;
        }
        //验证账号
        User u =userMapper.selectByName(user.getUsername());
        if(u!=null){
            map.put("usernameMsg","账号已经存在");
        }

        u =userMapper.selectByEmail(user.getEmail());
        if(u!=null){
            map.put("usernameMsg","邮箱已经存在");
        }

        //注册用户
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5(user.getPassword()+user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        Context context=new Context();
        context.setVariable("eamil",user.getEmail());
        //url http://localhost:8080/community/activation/101/code
        String url = domain + contextpath +"/activation/"+user.getId()+"/"+user.getActivationCode();
        context.setVariable("url",url);
        String content = templateEngine.process("/mail/activation",context);
        mailClient.sendEmail(user.getEmail(),"激活账号",content);
        return map;
    }

}
