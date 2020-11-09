package com.sss.sssforum.wxclient.dto;

import lombok.Data;

/**
 * @author lws
 * @date 2020-09-16 20:00
 **/
@Data
public class ResponseDTO {

    /**
     * 返回状态码
     */
    private Integer errcode;
    /**
     * 返回消息
     */
    private String errmsg;
}
