package com.sss.sssforum.wxclient.dto;

import com.sss.sssforum.wxclient.user.entity.WxUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author lws
 * @date 2020-08-10 13:45
 **/
@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO   {

    /**
     * 用户总数
     */
    private Integer userTotalCount;

    /**
     * 帖子总数
     */
    private Integer postTotalCount;
}
