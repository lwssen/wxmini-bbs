package com.sss.sssforum.enums;

/**
 *
 * @author lws
 * @date 2020-08-17 15:37
 **/
public enum FileResponseEnum {
    FILE_TYPE(3001,"暂不支持该文件类型的上传"),
    FILE_SIZE(3002,"请上传小于10M的文件"),
    FILE_EMPTY(3003,"文件不能为空")
    ;
    private Integer code;
    private String message;

    FileResponseEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
