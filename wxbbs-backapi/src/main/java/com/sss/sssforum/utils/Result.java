package com.sss.sssforum.utils;


import com.sss.sssforum.enums.ResponseDataEnum;

import java.util.HashMap;

/**
 *  统计返回结果类型类
 * @author SSS
 * @date 2019-12-25 15:12
 **/
public class Result<T> {

    /**
     * 状态码 200 成功 -1 失败
     */
    private Integer code;

    /**
     * 返回消息
     */
    private String message;

    /**
     * 返回数据对象
     */
    private T data;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Result() {
    }

    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    private Result(ResponseDataEnum responseDataEnum) {
        this.code = responseDataEnum.getCode();
        this.message = responseDataEnum.getMessage();
    }

    public static <T> Result<T> succes(){
        Result<T> result = new Result<>(ResponseDataEnum.SUCCESS);
        result.setData((T)new HashMap<>(1));
        return result;
    }
    public static <T> Result<T> succes(T data){
        Result<T> result = new Result<>(ResponseDataEnum.SUCCESS);
        result.setData(data);
        return result;
    }



    public static <T> Result<T> fail(){
        HashMap hashMap = new HashMap(1);
        Result result = new Result<>(ResponseDataEnum.FAIL);
        result.setData(hashMap);
        return result;
    }
    public static <T> Result<T> fail(String msg){
        Result<T> fail = fail();
        fail.setMessage(msg);
        return fail;
    }
    public static <T> Result<T> fail(ResponseDataEnum responseDataEnum){
        Result<T> fail = fail();
        fail.setMessage(responseDataEnum.getMessage());
        fail.setCode(responseDataEnum.getCode());
        return fail;
    }

    public static <T> Result<T> fail(Integer code,String msg){
        Result<T> fail = fail();
        fail.setCode(code);
        fail.setMessage(msg);
        return fail;
    }


}
