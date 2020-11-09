package com.sss.sssforum.wxclient.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.sql.Timestamp;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author sss
 * @since 2020-08-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class WxUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户名称
     */
    private String wxNickname;

    /**
     * 微信用户唯一标识
     */
    private String openId;

    /**
     * 角色ID
     */
    private Integer roleId;

    /**
     * 头像地址
     */
    private String avatarUrl;

    /**
     * 是否关注公众号 0 关注 1 未关注
     */
    private Boolean subscribe;

    /**
     * 用户地址
     */
    private String address;

    /**
     * 用户状态 0 正常
     */
    private Integer state;

    /**
     * 是否删除 0 正常 1 删除
     */
    @TableLogic
    private Boolean isDeleted;

    private Boolean isPush;

    /**
     * 创建时间
     */
    private Timestamp createTime;

    /**
     * 更新时间
     */
    private Timestamp updateTime;

    /**
     * 我的帖子总数
     */
    @TableField(exist = false)
    private Integer postTotalCount;


    /**
     * 我的点赞总数
     */
    @TableField(exist = false)
    private Integer likeTotalCount;

    public Integer getPostTotalCount() {
        return postTotalCount==null?0: postTotalCount;
    }

    public Integer getLikeTotalCount() {
        return likeTotalCount==null?0: likeTotalCount;
    }
}
