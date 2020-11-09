package com.sss.sssforum.wxclient.message.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.sql.Timestamp;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import java.io.Serializable;

import com.sss.sssforum.wxclient.user.entity.WxUser;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户消息表
 * </p>
 *
 * @author sss
 * @since 2020-08-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 评论内容ID 对应post_comment表的ID
     */
    private Long postCommentId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 帖子ID
     */
    private Long postId;
    /**
     * 点赞或回复评论或评论帖子的用户ID
     */
    private Long likeCommentUserId;

    /**
     * 不同类型消息的总条数
     */
    @TableField(exist = false)
    private Integer typeTotal;

    /**
     * 消息类型
     */
    private Integer messageType;

    /**
     * 是否已读 0 未读 1 已读
     */
    private Integer hasRead;

    /**
     * 是否删除 0 正常 1删除
     */
    @TableLogic
    private Boolean isDeleted;

    /**
     * 创建时间
     */
    private Timestamp createTime;

    /**
     * 更新时间
     */
    private Timestamp updateTime;




}
