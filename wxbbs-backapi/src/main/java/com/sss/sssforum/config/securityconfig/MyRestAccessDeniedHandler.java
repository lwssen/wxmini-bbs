package com.sss.sssforum.config.securityconfig;

import com.alibaba.fastjson.JSON;
import com.sss.sssforum.enums.ResponseDataEnum;
import com.sss.sssforum.utils.Result;
import com.sss.sssforum.utils.ServletUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 权限不足处理器
 *
 * @author lws
 * @date 2020-08-07 09:43
 **/
@Component
public class MyRestAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
        ServletUtils.renderString(httpServletResponse, JSON.toJSONString(Result.fail(ResponseDataEnum.NOTAUTHOR)));
    }
}
