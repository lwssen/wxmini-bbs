package com.sss.sssforum.exception;

import com.sss.sssforum.utils.Result;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * @author lws
 * @date 2020-08-01 18:39
 **/
@RestControllerAdvice
public class GloblaExceptionHandler {

    /**
     * 参数校验不通过异常处理
     * @param manv
     * @return java.lang.Object
     **/
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException manv){
        List<ObjectError> errors = manv.getBindingResult().getAllErrors();
        StringBuffer errorMsg=new StringBuffer();
        errors.stream().forEach(x -> errorMsg.append(x.getDefaultMessage()).append(";"));
        return Result.fail(errorMsg.toString());
    }

    @ExceptionHandler(CommonException.class)
    public Result commonExceptionHandler(CommonException ex){
        return Result.fail(ex.getCode(),ex.getMessage());
    }


//    @ExceptionHandler(Exception.class)
//    public Result exceptionHandler(Exception ex){
//        return Result.fail(5000,"内部服务器繁忙,请稍后再试/(ㄒoㄒ)/~~");
//    }
}
