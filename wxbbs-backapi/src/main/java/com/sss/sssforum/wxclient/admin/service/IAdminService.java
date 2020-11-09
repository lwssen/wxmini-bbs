package com.sss.sssforum.wxclient.admin.service;

import com.sss.sssforum.wxclient.dto.PostCommentDTO;

import java.util.List;

/**
 * @author lws
 * @date 2020-09-08 08:43
 **/
public interface IAdminService {


    /**
     * 屏蔽评论内容
     *
     * @param postCommentDTO
     * @return boolean
     * @author lws
     * @date 2020/9/8 8:45
    **/
    String shieldingCommentContent(PostCommentDTO postCommentDTO);

    /**
     * 评论内容置顶
     *
     * @param postCommentDTO
     * @return boolean
     * @author lws
     * @date 2020/9/8 8:46
    **/
    boolean setCommentContentTop(PostCommentDTO postCommentDTO);

    /**
     * 管理帖子下的所有评论接口
     *
     * @param postId
     * @param commentContent
     * @return java.util.List<com.sss.sssforum.wxclient.dto.PostCommentDTO>  {@link }
     * @author lws
    **/
    List<PostCommentDTO> getAllPostCommentList(Long postId, String commentContent );

}
