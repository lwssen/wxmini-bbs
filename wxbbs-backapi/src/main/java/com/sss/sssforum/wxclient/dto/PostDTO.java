package com.sss.sssforum.wxclient.dto;

import com.sss.sssforum.wxclient.post.entity.ForumPost;
import com.sss.sssforum.wxclient.user.entity.WxUser;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author lws
 * @date 2020-08-07 17:02
 **/
@Data
@Accessors(chain = true)
public class PostDTO extends ForumPost {

    private WxUser wxUser;
    private Long userId;

    /**
     * 是否点赞 true 是 false 不是
     */
    private Boolean isLike=Boolean.FALSE;
    private String typeName;
}
