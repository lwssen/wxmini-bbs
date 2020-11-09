package com.sss.sssforum.exception;

/**
 * 自定义异常
 *
 * @author lws
 * @date 2020-08-17 15:29
 **/
public class CommonException extends RuntimeException {
    private Integer code;

    public CommonException(Integer code,String message) {
        super(message);
        this.code = code;

    }
    public CommonException(String message) {
        super(message);
    }

    public Integer getCode() {
        return code;
    }
}
