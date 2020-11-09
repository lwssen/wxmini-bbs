package com.sss.sssforum.wxclient.post.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.sql.Timestamp;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * <p>
 * 帖子评论表
 * </p>
 *
 * @author sss
 * @since 2020-08-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class PostComment implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 帖子ID 对应forum_post表的ID
     */
    @NotNull(message = "帖子ID不能为空")
    private Long postId;

    /**
     * 父级ID
     */
    private Integer parentId;

    /**
     * 评论内容
     */
    @NotBlank(message = "评论内容不能为空")
    private String commentContent;

    /**
     * 替换前的评论内容
     */
    private String oldCommentContent;

    /**
     * 评论人ID
     */
    private Long userId;

    /**
     * 1 他人评论或回复  2 点赞
     */
    private Integer commentType;

    /**
     * 排序
    **/
    private Integer orderNumber;

    /**
     * 评论点赞数
     */
    private Integer commentLikeCount;

    /**
     * 是否删除 0 正常  1 删除
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

    /**
     * 消息类型
     */
    @TableField(exist = false)
    private Integer messageType;
    /**
     * 帖子发布人ID
     */
    @TableField(exist = false)
    private Long publishUserId;

    /**
     * 通知新消息的用户ID
     */
    @TableField(exist = false)
    private Long getMessageUserId;

}
