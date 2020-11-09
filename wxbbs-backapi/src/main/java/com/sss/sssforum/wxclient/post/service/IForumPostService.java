package com.sss.sssforum.wxclient.post.service;

import com.sss.sssforum.wxclient.dto.PostDTO;
import com.sss.sssforum.wxclient.dto.PostTypeDTO;
import com.sss.sssforum.wxclient.dto.UserMessageDTO;
import com.sss.sssforum.wxclient.post.entity.ForumPost;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 所有帖子数据表 服务类
 * </p>
 *
 * @author sss
 * @since 2020-07-28
 */
public interface IForumPostService extends IService<ForumPost> {



    List<PostDTO> getPostList(  Integer typeId,Boolean isDeleted,String title);
    /**
     *  帖子回复数增加
     *
     * @param postId 帖子ID
     * @return int
     * @date 2020/8/4 8:41
     **/
     Boolean increCommentcount(Long postId);

    /**
     *  帖子阅读数增加
     *
     * @param postId 帖子ID
     * @return int
     * @date 2020/8/4 8:41
     **/
     Boolean increViewCount(Long postId);

    /**
     *  帖子点赞数增加
     *
     * @param postId 帖子ID
     * @return int
     * @date 2020/8/4 8:41
     **/
    Integer  increLikeCount(ForumPost forumPost);

    /**
     *  帖子点赞数减少
     *
     * @param postId 帖子ID
     * @return int
     * @date 2020/8/4 8:41
     **/
    Integer  decreLikeCount(ForumPost forumPost);


    /**
     * 是否删除帖子
     *
     * @param postId 帖子ID
     * @param isDeleted  帖子状态
     * @return java.lang.Integer  {@link }
     * @author lws
    **/
    Integer updatePostStatus(Long  postId,  Integer isDeleted);
    Integer updatePost(PostTypeDTO postTypeDTO);

    /**
     * 点赞帖子
     *
     * @param userMessageDTO
     * @return com.sss.sssforum.wxclient.dto.UserMessageDTO
     * @author lws
     * @date 2020/8/14 16:52
    **/
    UserMessageDTO likePost( UserMessageDTO userMessageDTO);

    /**
     * 文件上传
     *
     * @param file 文件
     * @return java.lang.String
     * @author lws
     * @date 2020/8/17 15:19
    **/
    String uploadFile(MultipartFile file);

    /**
     * 阿里云OSS文件上传
     *
     * @param file  文件
     * @return java.lang.String  {@link }
     * @author lws
    **/
    String ossUploadFile(MultipartFile file);



}
