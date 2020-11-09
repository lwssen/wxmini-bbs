package com.sss.sssforum.wxclient.post.service;

import com.sss.sssforum.wxclient.dto.PostCommentDTO;
import com.sss.sssforum.wxclient.dto.UserMessageDTO;
import com.sss.sssforum.wxclient.post.entity.PostComment;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * 帖子评论表 服务类
 * </p>
 *
 * @author sss
 * @since 2020-08-03
 */
public interface IPostCommentService extends IService<PostComment> {

    /**
     * 发布评论
     *
     * @param postComment
     * @return java.lang.Long
     * @author sss
     * @date 2020/8/4 9:50
    **/
    Long pushComment(PostComment postComment);

    List<PostCommentDTO> getPostCommentList(Long postId,String commentContent );

    /**
     * 点赞帖子评论
     *
     * @param userMessageDTO
     * @return com.sss.sssforum.wxclient.dto.UserMessageDTO
     * @author lws
     * @date 2020/8/14 16:52
     **/
    UserMessageDTO likePostComment(UserMessageDTO userMessageDTO);


    /**
     *  某一楼层的更多回复的数据集合
     *
     * @param postCommentDTO
     * @return java.util.List<com.sss.sssforum.wxclient.dto.PostCommentDTO>
     * @author lws
     * @date 2020/8/19 14:20
     **/
    List<PostCommentDTO>  showManyComment(PostCommentDTO postCommentDTO);

}
