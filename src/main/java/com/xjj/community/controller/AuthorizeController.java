package com.xjj.community.controller;

import com.xjj.community.Provider.GitHubProvider;
import com.xjj.community.dto.AccessTokenDTO;
import com.xjj.community.dto.GitHubUser;
import com.xjj.community.mapper.UserMapper;
import com.xjj.community.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@Controller
public class AuthorizeController {

    @Autowired
    private GitHubProvider gitHubProvider;

    @Autowired
    private UserMapper userMapper;

    @Value("${github.client.id}")
    private  String clientId;

    @Value("${github.client.secret}")
    private  String clientSecret;

    @Value("${github.redirect.uri}")
    private  String redirectUri;

    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code")String code,
                           @RequestParam(name = "state")String state,
                           HttpServletRequest request)
    {
        AccessTokenDTO accessTokenDTO=new AccessTokenDTO();
        accessTokenDTO.setCode(code);
        accessTokenDTO.setState(state);
        accessTokenDTO.setRedirect_uri(redirectUri);
        accessTokenDTO.setClient_id(clientId);
        accessTokenDTO.setClient_secret(clientSecret);
        String accessToken= gitHubProvider.GetAccessToken(accessTokenDTO);

        System.out.println("已经获取到token"+accessToken);

        GitHubUser gitHubUser=gitHubProvider.getUser(accessToken);

        System.out.println(gitHubUser.getName());

        if(gitHubUser!=null){
            User user=new User();
            user.setToken(UUID.randomUUID().toString());
            user.setName(gitHubUser.getName());
            user.setAccountId(String.valueOf(gitHubUser.getId()));
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());


            userMapper.insert(user);
            //登录成功，写cookie 和 session
            request.getSession().setAttribute("user",gitHubUser);

            //重定向
            return "redirect:/";



        }else {
            //登录失败，重新登录

            //重定向
            return "redirect:/";
        }

    }


}
