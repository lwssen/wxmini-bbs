package com.sss.sssforum.task;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceOkHttpImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sss.sssforum.config.redisconfig.RedisUtil;
import com.sss.sssforum.config.wxconfig.WxminiappConfig;
import com.sss.sssforum.constant.CommonConstant;
import com.sss.sssforum.utils.MyDateTimeUtils;
import com.sss.sssforum.utils.SensitiveWordUtil;
import com.sss.sssforum.wxclient.post.entity.PostComment;
import com.sss.sssforum.wxclient.post.service.IPostCommentService;
import com.sss.sssforum.wxclient.sensitiveword.entity.SensitiveWord;
import com.sss.sssforum.wxclient.sensitiveword.service.ISensitiveWordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lws
 * @date 2020-09-15 21:19
 **/
//@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class SensitiveWordTaskCheck {
    private final IPostCommentService postCommentService;
    private final RedisUtil redisUtil;
    private final ISensitiveWordService sensitiveWordService;

    private final String appId = "wx77f2306860352fed";
    /**
     * corn表达式设置 每隔一分钟执行一次
     *
     * @return void
     * @author lws
     **/
   // @Scheduled(cron = "0 0/1 * * * ?  ")
    public void contentCheck(){
        List<String> list = (List<String>) redisUtil.lRange(CommonConstant.SENSITIVEWORD_LIST, 0, -1);
        if (CollectionUtils.isEmpty(list)) {
            List<SensitiveWord> sensitiveWordList = sensitiveWordService.list(new QueryWrapper<SensitiveWord>().select("sensitive_word"));
            if (!CollectionUtils.isEmpty(sensitiveWordList)) {
                List<String> strings = sensitiveWordList.stream().map(SensitiveWord::getSensitiveWord).collect(Collectors.toList());
                redisUtil.rPushAll(CommonConstant.SENSITIVEWORD_LIST,strings);
                list.addAll(strings);
            }
        }
        ArrayList<PostComment> arrayList = new ArrayList<>();
        for (String s : list) {
            List<PostComment> postComments = postCommentService.list(new LambdaQueryWrapper<PostComment>()
                    .select(PostComment::getId, PostComment::getCommentContent).like(PostComment::getCommentContent, s));
            if (CollectionUtils.isNotEmpty(postComments)) {
                for (PostComment postComment : postComments) {
                     String oldContent = postComment.getCommentContent();
                    String newContent = SensitiveWordUtil.replaceSensitiveWord(oldContent, CommonConstant.SENSITIVEWORD_REPLACE);
                    postComment.setCommentContent(newContent);
                    arrayList.add(postComment);
                }
            }
        }
       // log.info("本次脱敏数据共 {} 条",arrayList.size());
        if (CollectionUtils.isNotEmpty(arrayList)) {
            postCommentService.updateBatchById(arrayList);
        }
    }

    /**
     * 定时刷新token
     *
     * @param
     * @return void  {@link }
     * @author lws
    **/
   // @Scheduled(cron = "0 0/10 * * * ?  ")
    public void refreshAccessToken() {

        final WxMaService wxService = WxminiappConfig.getMaService(appId);
        String temp =(String)redisUtil.get(CommonConstant.EXPIRE_TIME);
        Long second = Long.valueOf(temp);
        LocalDateTime now = LocalDateTime.now();
        Long nowSecond = MyDateTimeUtils.getSecond(now);
        try {
            if (nowSecond > second){
                String accessToken = wxService.getAccessToken();
                redisUtil.set(CommonConstant.WX_TOKEN,accessToken);
                LocalDateTime plusMinutes = now.plusMinutes(90);
                Long newSecond = MyDateTimeUtils.getSecond(plusMinutes);
                redisUtil.set(CommonConstant.EXPIRE_TIME,String.valueOf(newSecond));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
