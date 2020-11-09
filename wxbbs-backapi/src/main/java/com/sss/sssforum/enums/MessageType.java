package com.sss.sssforum.enums;

/**
 * 消息类型 枚举
 *
 * @author lws
 * @date 2020-08-12 16:22
 **/
public enum MessageType {

    OTHER_COMMENT(1,"他人评论帖子"),
    REPLY_COMMENT(2,"回复评论"),
    GOOD_LIKE(3,"点赞"),
    SYSTEM(4,"系统通知"),
    NONEXISTENT(0,"代表不存在 仅用于作标识");

    private Integer code;
    private String message;

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    MessageType(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
