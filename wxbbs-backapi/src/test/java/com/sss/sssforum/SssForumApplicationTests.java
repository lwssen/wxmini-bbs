package com.sss.sssforum;

import cn.binarywang.wx.miniapp.api.WxMaService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sss.sssforum.config.redisconfig.RedisUtil;
import com.sss.sssforum.config.wxconfig.WxminiappConfig;
import com.sss.sssforum.constant.CommonConstant;
import com.sss.sssforum.utils.JsonUtils;
import com.sss.sssforum.utils.MyDateTimeUtils;
import com.sss.sssforum.wxclient.dto.ResponseDTO;
import com.sss.sssforum.wxclient.post.entity.PostComment;
import com.sss.sssforum.wxclient.post.service.IPostCommentService;
import com.sss.sssforum.wxclient.sensitiveword.entity.SensitiveWord;
import com.sss.sssforum.wxclient.sensitiveword.service.ISensitiveWordService;
import me.chanjar.weixin.common.error.WxErrorException;
import okhttp3.*;
import org.apache.commons.collections.CollectionUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@SpringBootTest
class SssForumApplicationTests {
    @Autowired
    private   RedisUtil redisUtil;
    @Autowired
    private ISensitiveWordService sensitiveWordService;
    @Autowired
    private IPostCommentService postCommentService;
    @Test
    void contextLoads() {
        List<String> strings = new ArrayList<>();
        strings.add("垃圾");
        redisUtil.rPush(CommonConstant.SENSITIVEWORD_LIST,"垃圾");
        List<String> qq = (List<String>) redisUtil.lRange(CommonConstant.SENSITIVEWORD_LIST, 0, -1);
        qq.forEach(System.out::println);
        System.out.println(redisUtil.lRange(CommonConstant.SENSITIVEWORD_LIST,0 ,-1));
    }
    @Test
    void contextLoads2() {
        redisUtil.lPush("qq","哈哈哈");
        List<String> qq = (List) redisUtil.lRange("qq", 0, -1);
        qq.forEach(System.out::println);
        System.out.println(qq.getClass());
    }
    @Test
    void contextLoads3() {
//        List<SensitiveWord> sensitiveWordList = sensitiveWordService.list(new QueryWrapper<SensitiveWord>().select("sensitive_word"));
//        if (!CollectionUtils.isEmpty(sensitiveWordList)) {
//            List<String> strings = sensitiveWordList.stream().map(SensitiveWord::getSensitiveWord).collect(Collectors.toList());
//            redisUtil.rPushAll(CommonConstant.SENSITIVEWORD_LIST,strings);
//          //  list.addAll(strings);
//        }
//        Long o = redisUtil.lLength(CommonConstant.SENSITIVEWORD_LIST);
//        System.out.println(o);
//        Integer obj = (Integer) postCommentService.getObj(new QueryWrapper<PostComment>().select("max(order_number) as orderNumber"), Function.identity());
//        System.out.println(obj);

        final WxMaService wxService = WxminiappConfig.getMaService("wx77f2306860352fed");

        try {
//            String accessToken = wxService.getAccessToken();
//            redisUtil.set("wx:token",accessToken);
//            Long milliSecond = MyDateTimeUtils.getMilliSecond(LocalDateTime.now().plusMinutes(100));
//            String s1 = String.valueOf(milliSecond);
//            redisUtil.set(CommonConstant.EXPIRE_TIME,s1);
//            System.out.println(MyDateTimeUtils.getMilliSecond(LocalDateTime.now()));
//            System.out.println(System.currentTimeMillis());
//            System.out.println(MyDateTimeUtils.getMilliSecond(LocalDateTime.now().plusMinutes(100)));

           String  accessToken =(String)redisUtil.get("wx:token");
          // String s2 =( String) redisUtil.get(CommonConstant.EXPIRE_TIME);
           // LocalDateTime localDateTime = JsonUtils.jsonToObject(s2, LocalDateTime.class);
          //  System.out.println("token刷新时间"+MyDateTimeUtils.DATETIME_FORMATTER.format(localDateTime));
            System.out.println("获取到的token：等于"+accessToken);
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("content","仅有免费成人网站哇哇哇哇");
            String s = JsonUtils.toJson(hashMap);
            String url="https://api.weixin.qq.com/wxa/msg_sec_check?access_token="+accessToken;
            String post = post(url, s);
            JsonUtils.jsonToObject(post, ResponseDTO.class);
            System.out.println("内容检测返回结果："+post);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient();

    String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

}
