package com.sss.sssforum.wxclient.dto;

import com.sss.sssforum.wxclient.post.entity.PostComment;
import com.sss.sssforum.wxclient.user.entity.WxUser;
import lombok.Data;

import java.util.List;

/**
 * @author lws
 * @date 2020-08-07 20:41
 **/
@Data
public class PostCommentDTO extends PostComment {

    private WxUser wxUser;
    private WxUser answer;

    /**
     * 更多回复的条数
     */
    private Integer manyCommentCount;
    /**
     * 子评论集合
     */
    private List<PostCommentDTO> childCommentList;
    private String childWxNickname;
    private String childAvatarUrl;
    private Boolean isLike;
}
