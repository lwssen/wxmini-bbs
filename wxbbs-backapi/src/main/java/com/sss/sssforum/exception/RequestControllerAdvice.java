package com.sss.sssforum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author lws
 * @date 2020-11-04 09:50
 **/
@Slf4j
@ControllerAdvice(basePackages = "com.sss.sssforum.wxclient.user")
public class RequestControllerAdvice implements RequestBodyAdvice {


    /**
     * 该方法用于判断当前请求，是否要执行beforeBodyRead方法
     * @param methodParameter handler方法的参数对象
     * @param type handler方法的参数类型
     * @param aClass 将会使用到的Http消息转换器类类型
     * @return 返回true则会执行beforeBodyRead
     */
    @Override
    public boolean supports(MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        // 此处true代表执行当前advice的业务，false代表不执行
        return true;
    }

    /**
     * 读取参数前执行 在Http消息转换器执转换，之前执行
     *
     * @param httpInputMessage
     * @param methodParameter
     * @param type
     * @param aClass
     * @return
     * @throws IOException
     */
    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) throws IOException {
        log.info("-----beforeBodyRead-----");
        return httpInputMessage;
    }

    /**
     * 读取参数后执行 在Http消息转换器执转换，之后执行
     *
     * @param o 转换后的对象
     * @param httpInputMessage 客户端的请求数据
     * @param methodParameter handler方法的参数类型
     * @param type handler方法的参数类型
     * @param aClass 使用的Http消息转换器类类型
     * @return  返回一个新的对象
     */
    @Override
    public Object afterBodyRead(Object o, HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        log.info("-----afterBodyRead-----");
        log.info("class name is {}",methodParameter.getDeclaringClass().getSimpleName());
        log.info("method name is {}",methodParameter.getMethod().getName());
        log.info("request parameter is {}",o.toString());
        return null;
    }

    /**
     * 无请求时的处理 不过这个方法处理的是，body为空的情况
     *
     * @param o
     * @param httpInputMessage
     * @param methodParameter
     * @param type
     * @param aClass
     * @return
     */
    @Override
    public Object handleEmptyBody(Object o, HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        log.info("-----handleEmptyBody-----");
        return o;
    }
}
