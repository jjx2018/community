package com.xjj.community.Provider;

import com.alibaba.fastjson.JSON;
import com.xjj.community.dto.AccessTokenDTO;
import com.xjj.community.dto.GitHubUser;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GitHubProvider {

    public String GetAccessToken(AccessTokenDTO accessTokenDTO) {

        MediaType mediaType = MediaType.get("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(mediaType, JSON.toJSONString(accessTokenDTO));
        Request request = new Request.Builder()
                .url("https://github.com/login/oauth/access_token")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            //System.out.println(response.body().string());

            String string=response.body().string();
            String tokenStr=string.split("&")[0];
            System.out.println(tokenStr);

            String token=tokenStr.split("=")[1];
            System.out.println(token);
            return token;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public GitHubUser getUser(String accessToken) {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.github.com/user?access_token=" + accessToken)
                .build();

        try

        {
            Response response = client.newCall(request).execute();
            String string=response.body().string();
            System.out.println(string);
            GitHubUser gitHubUser = JSON.parseObject(string, GitHubUser.class);

            return gitHubUser;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }


}
