package com.sss.sssforum.aspect;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sss.sssforum.annotation.SensitiveWordCheck;
import com.sss.sssforum.config.redisconfig.RedisUtil;
import com.sss.sssforum.constant.CommonConstant;
import com.sss.sssforum.utils.SensitiveWordUtil;
import com.sss.sssforum.wxclient.post.entity.ForumPost;
import com.sss.sssforum.wxclient.post.entity.PostComment;
import com.sss.sssforum.wxclient.sensitiveword.entity.SensitiveWord;
import com.sss.sssforum.wxclient.sensitiveword.service.ISensitiveWordService;
import com.sss.sssforum.wxclient.user.entity.WxUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author lws
 * @date 2020-08-16 12:48
 **/
@Aspect
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class SensitiveWordCheckAspect {
    private final RedisUtil redisUtil;
    private final ISensitiveWordService sensitiveWordService;

    @Pointcut("@annotation(com.sss.sssforum.annotation.SensitiveWordCheck)")
    public void sensitiveWordPointCut(){

    }

    @Before("sensitiveWordPointCut()")
    public void myBeforeCut(JoinPoint joinPoint) {
        List<String> list = (List<String>) redisUtil.lRange(CommonConstant.SENSITIVEWORD_LIST, 0, -1);
        if (CollectionUtils.isEmpty(list)) {
            List<SensitiveWord> sensitiveWordList = sensitiveWordService.list(new QueryWrapper<SensitiveWord>().select("sensitive_word"));
            if (!CollectionUtils.isEmpty(sensitiveWordList)) {
                List<String> strings = sensitiveWordList.stream().map(SensitiveWord::getSensitiveWord).collect(Collectors.toList());
                redisUtil.rPushAll(CommonConstant.SENSITIVEWORD_LIST,strings);
                list.addAll(strings);
            }
        }
        Set<String> stringSet = new HashSet<>(list);
        SensitiveWordUtil.init(stringSet);
//        Signature signature = joinPoint.getSignature();
//        MethodSignature methodSignature = (MethodSignature) signature;
//        String name = methodSignature.getName();
//        log.info("方法名称等于:{}",name);
        //获取方法传递的参数
        Object[] args = joinPoint.getArgs();
        if (null != args) {
            for (Object o : args) {
                if (o instanceof PostComment) {
                    String oldCommentContent = ((PostComment) o).getCommentContent();
                    String newCommentContent = SensitiveWordUtil.replaceSensitiveWord(oldCommentContent, CommonConstant.SENSITIVEWORD_REPLACE);
                    ((PostComment) o).setCommentContent(newCommentContent);
                }else if (o instanceof ForumPost){
                   String oldTitle = ((ForumPost) o).getTitle();
                    String newTitle = SensitiveWordUtil.replaceSensitiveWord(oldTitle, CommonConstant.SENSITIVEWORD_REPLACE);
                    ((ForumPost) o).setTitle(newTitle);
                    String oldPostCotent = ((ForumPost) o).getPostCotent();
                    String newPostCotent = SensitiveWordUtil.replaceSensitiveWord(oldPostCotent, CommonConstant.SENSITIVEWORD_REPLACE);
                    ((ForumPost) o).setPostCotent(newPostCotent);
                } else if (o instanceof WxUser) {
                    String oldWxNickname = ((WxUser) o).getWxNickname();
                    String newWxNickname = SensitiveWordUtil.replaceSensitiveWord(oldWxNickname, CommonConstant.SENSITIVEWORD_REPLACE);
                    ((WxUser) o).setWxNickname(newWxNickname);
                }
            }
        }
    }
}
