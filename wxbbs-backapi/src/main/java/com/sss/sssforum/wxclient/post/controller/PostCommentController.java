package com.sss.sssforum.wxclient.post.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sss.sssforum.annotation.SensitiveWordCheck;
import com.sss.sssforum.base.BaseController;
import com.sss.sssforum.config.properties.FileProperties;
import com.sss.sssforum.utils.IdWorker;
import com.sss.sssforum.utils.MyDateTimeUtils;
import com.sss.sssforum.utils.MyFileUtils;
import com.sss.sssforum.utils.Result;
import com.sss.sssforum.wxclient.dto.PostCommentDTO;
import com.sss.sssforum.wxclient.dto.UserMessageDTO;
import com.sss.sssforum.wxclient.post.entity.PostComment;
import com.sss.sssforum.wxclient.post.service.IForumPostService;
import com.sss.sssforum.wxclient.post.service.IPostCommentService;
import javafx.geometry.Pos;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 *
 * 帖子评论接口列表
 *
 *
 * @author sss
 * @since 2020-08-03
 */
@RestController
@RequestMapping("/post-comment")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PostCommentController extends BaseController {

    private final IPostCommentService postCommentService;
    private final IForumPostService forumPostService;


    /**
     * 根据帖子ID查询评论列表
     *
     * @param postId  帖子ID
     * @return com.sss.sssforum.utils.Result<java.util.List<com.sss.sssforum.wxclient.post.entity.PostComment>>  {@link }
     * @author lws
    **/
    @GetMapping("/list/{postId}")
    public Result<PageInfo<PostCommentDTO>> getPostCommentList(@PathVariable Long postId,@RequestParam(required = false) String commentContent ){
        startPage();
        List<PostCommentDTO> postComments = postCommentService.getPostCommentList(postId,commentContent);
        PageInfo<PostCommentDTO> pageInfo = new PageInfo<>(postComments);
        return Result.succes(pageInfo);
    }

    /**
     *  某一楼层的更多回复的数据集合
     *
     * @param postCommentDTO
     * @return java.util.List<com.sss.sssforum.wxclient.dto.PostCommentDTO>
     * @author lws
     * @date 2020/8/19 14:20
     **/
    @GetMapping("/show/many/comment")
    public Result<List<PostCommentDTO>> showManyComment(PostCommentDTO postCommentDTO){
        return Result.succes(postCommentService.showManyComment(postCommentDTO));
    }
    /**
     * 发布评论
     *
     * @param postComment
     * @return com.sss.sssforum.utils.Result<java.lang.Long>  {@link }
     * @author lws
    **/
    @PostMapping("/push")
    @SensitiveWordCheck
    public Result<Long> pushComment(@RequestBody @Valid PostComment postComment){
        return Result.succes(postCommentService.pushComment(postComment));
    }

    /**
     *  点赞他人评论或回复
     *
     * @param userMessageDTO
     * @return com.sss.sssforum.utils.Result<com.sss.sssforum.wxclient.dto.UserMessageDTO>
     * @author lws
     * @date 2020/8/14 17:15
    **/
    @PutMapping("/like")
    public Result<UserMessageDTO> likePostComment(@RequestBody UserMessageDTO userMessageDTO){
        return Result.succes(postCommentService.likePostComment(userMessageDTO));
    }



}

