package com.sss.sssforum.utils;

import com.sss.sssforum.wxclient.dto.ResponseDTO;
import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;

/**
 * 小程序内容检测
 *
 * @author lws
 * @date 2020-09-16 20:03
 **/
public class WxMiniContentCheckUtils {

    private final Integer errcode=87014;

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();
    public  boolean isPass(String accessToken, String content){

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("content",content);
        String jsonStr = JsonUtils.toJson(hashMap);
        String url="https://api.weixin.qq.com/wxa/msg_sec_check?access_token="+accessToken;
        String post = post(url, jsonStr);
        ResponseDTO responseDTO = JsonUtils.jsonToObject(post, ResponseDTO.class);
        if (errcode.equals(responseDTO.getErrcode())) {
            return false;
        }
       return true;

    }

   String post(String url, String json)  {
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
   }
}
