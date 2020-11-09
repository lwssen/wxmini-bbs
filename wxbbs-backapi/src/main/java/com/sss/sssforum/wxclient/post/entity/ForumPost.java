package com.sss.sssforum.wxclient.post.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import java.sql.Timestamp;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.sss.sssforum.wxclient.user.entity.WxUser;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 * 所有帖子数据表
 * </p>
 *
 * @author sss
 * @since 2020-07-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ForumPost  implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 帖子ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 类别id
     */
    private Integer typesId;

    /**
     * 帖子简介
     */
    private String digest;

    /**
     * 标题
     */
    @NotBlank(message = "标题不能为空")
    private String title;

    /**
     * 帖子内容
     */
    @NotBlank(message = "内容不能为空")
    private String postCotent;

    /**
     * 发布人ID
     */
    private Long createUserId;

    /**
     * 回复条数
     */
    private Integer commentCount;

    /**
     * 查看次数
     */
    private Integer viewCount;

    /**
     * 排序数字
     */
    private Integer orderNumber;

    /**
     * 点赞次数
     */
    private Integer likeCount;

    /**
     * 是否删除 0 正常 1 删除
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

    @TableField(exist = false)
    private WxUser wxUser;

    @TableField(exist = false)
    private Boolean isLike;

}
