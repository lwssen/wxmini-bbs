package com.sss.sssforum.enums;

/**
 * 返回类型枚举状态码
 * @author SSS
**/
public enum ResponseDataEnum {

    /**
     * 成功状态
     */
    SUCCESS(200,"ok"),

    NOTAUTHOR(444,"权限不足"),
    EXPIRED(445,"token已过期或不存在"),
    /**
     * 失败状态
     */
    FAIL(-1,"error")
    ;

    private Integer code;

    private String message;

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    ResponseDataEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
