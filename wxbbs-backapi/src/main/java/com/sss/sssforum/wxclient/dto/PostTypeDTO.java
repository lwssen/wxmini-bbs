package com.sss.sssforum.wxclient.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author lws
 * @date 2020-08-09 18:19
 **/
@Data
public class PostTypeDTO {

    /**
     * 帖子ID
     */
    @NotEmpty(message = "帖子ID 不能为空")
    private Long postId;
    /**
     * 收到删帖消息的用id
     */
    private Long getMessageUserId;

    /**
     * 类型ID
     */
    @NotEmpty(message = "类型ID 不能为空")
    private Integer typesId;

    /**
     * 是否删除 true 已删除 false 未删除
     */
    private Boolean isDeleted;
}
