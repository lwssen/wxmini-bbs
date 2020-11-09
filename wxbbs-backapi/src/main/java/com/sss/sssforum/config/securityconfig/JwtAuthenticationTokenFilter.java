package com.sss.sssforum.config.securityconfig;

import com.alibaba.fastjson.JSON;
import com.sss.sssforum.config.springconfig.JwtHelper;
import com.sss.sssforum.enums.ResponseDataEnum;
import com.sss.sssforum.utils.Result;
import com.sss.sssforum.utils.SecurityUtils;
import com.sss.sssforum.utils.ServletUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author lws
 * @date 2020-08-06 22:35
 **/
@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter{
    @Value("${server.servlet.context-path}")
    private String contextPath;
    @Value("${myIgnore.urls}")
    private String ignoreUrls;

    private final UserDetailsService userDetailsService;
    private final JwtHelper jwtHelper;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        cosrFilter(response);
        String authorToken = request.getHeader("Author-Token");
        if (StringUtils.isEmpty(authorToken)) {
            ServletUtils.renderString(response, JSON.toJSONString(Result.fail(ResponseDataEnum.EXPIRED)));
            return;
        }
        if (AuthLogin(request, response, authorToken)) {
            return;
        }
//        String openId = "oKDvewjopFLzdAobDPEU3c7gxno8";
//            UserDetails details = null;
//            if (!StringUtils.isEmpty(openId)) {
//                details = userDetailsService.loadUserByUsername(openId);
//            }
//            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
//            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        chain.doFilter(request, response);
    }

    private boolean AuthLogin(HttpServletRequest request, HttpServletResponse response, String authorToken) {
        if (StringUtils.isNotBlank(authorToken)) {
            if (!jwtHelper.validateTokenIgnoreException(authorToken)) {
                ServletUtils.renderString(response, JSON.toJSONString(Result.fail(ResponseDataEnum.EXPIRED)));
                return true;
            }
            Claims claims = jwtHelper.getClaimsFromToken(authorToken);
            String openId = (String) claims.get("openId");
            UserDetails details = null;
            if (!StringUtils.isEmpty(openId)) {
                details = userDetailsService.loadUserByUsername(openId);
            }
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        return false;
    }

    /**
     *  过滤指定 URL不执行 doFilterInternal方法
     *
     * @param request
     * @return boolean
     * @author lws
     * @date 2020/8/7 14:33
    **/
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String requestURI = request.getRequestURI();
        //需要执行doFilterInternal()方法的接口
        List<String> authUrls = Arrays.asList("/posts/push",
                "/posts/like", "/posts/image/upload",
               "/post-comment/like", "/post-comment/push", "/my",
                "/user-messages/count",
                "/user-messages/type/count",
                "/user-messages/clear",
                "/user-messages");
        for (String authUrl : authUrls) {
            if (requestURI.startsWith(contextPath+authUrl)) {
                return false;
            }
        }
        String authorToken = request.getHeader("Author-Token");
        if (StringUtils.isNotBlank(authorToken)) {
            if (requestURI.endsWith("/login") || requestURI.startsWith(contextPath+"/wx/user/save")
                    || requestURI.startsWith(contextPath+"/wx/user/get/token")){
                return  true;
            }
            if (!jwtHelper.validateTokenIgnoreException(authorToken)) {
                return false;
            }
            Claims claims = jwtHelper.getClaimsFromToken(authorToken);
            String openId = (String) claims.get("openId");
            UserDetails details = null;
            if (!StringUtils.isEmpty(openId)) {
                details = userDetailsService.loadUserByUsername(openId);
            }
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        return true;
        //不需要执行doFilterInternal()方法的接口
//        String[] strings = ignoreUrls.split(",");
//        List<String> ignoreUrls = Arrays.asList(strings);
//        String requestURI = request.getRequestURI();
//        for (String ignoreUrl : ignoreUrls) {
//            if (requestURI.startsWith(contextPath+ignoreUrl)) {
//                return true;
//            }
//        }
//
//        return false;
    }

    private void  cosrFilter(HttpServletResponse response){
        response.setHeader("Access-Control-Allow-Origin", "*");

        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, HEAD,PUT");

        response.setHeader("Access-Control-Max-Age", "3600");

        response.setHeader("Access-Control-Allow-Headers", "access-control-allow-origin, authority, " +
                "content-type, version-info, X-Requested-With,accessToken,Authorization,Author-Token");
    }
}
