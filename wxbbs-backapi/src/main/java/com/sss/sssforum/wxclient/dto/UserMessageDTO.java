package com.sss.sssforum.wxclient.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.sss.sssforum.wxclient.post.entity.PostComment;
import com.sss.sssforum.wxclient.user.entity.WxUser;
import lombok.*;
import lombok.experimental.Accessors;

import java.sql.Timestamp;

/**
 * @author lws
 * @date 2020-08-04 10:13
 **/
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserMessageDTO {

    private Long id;

    /**
     * 评论内容ID 对应post_comment表的ID
     */
    private Long postCommentId;

    /**
     * 评论内容
     */
    private String commentContent;
    /**
     * 他人评论的回复内容
     */
    private String otherCommentContent;


    /**
     * 消息内容
     */
    private String messageContent;

    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 点赞的评论ID
     */
    private Long parentId;
    /**
     * 收到消息的用户ID
     */
    private Long getMessageUserId;


    /**
     * 帖子或评论或点赞  userId
     */
    private Long publishUserId;
    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 评论或回复收到的消息数
     */
    private Integer messageCount;


    /**
     * 系统通知消息数
     */
    private Integer notificationCount;

    /**
     * 是否点赞 true 是 false 不是
     */
    private Boolean isLike;

    /**
     * 用户名字
     */
    private String userName;

    /**
     * 帖子ID
     */
    private Long postId;

    /**
     * 帖子标题
     */
    private String title;

    /**
     * 消息类型
     */
    private Integer messageType;

    /**
     * 创建时间
     */
    private Timestamp createTime;

    private WxUser wxUser;
    private PostComment postComment;

}
