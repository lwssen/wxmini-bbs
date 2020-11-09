package com.sss.sssforum.wxclient.wechat.gongzhonghao;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.sss.sssforum.config.springconfig.JwtHelper;
import com.sss.sssforum.config.securityconfig.MyUserDetailsService;
import com.sss.sssforum.config.wxconfig.WechatConfig;
import com.sss.sssforum.wxclient.user.entity.WxUser;
import com.sss.sssforum.wxclient.user.service.IWxUserService;
import com.sss.sssforum.wxclient.wechat.message.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信公众号授权和登录接口列表
 *
 * @author lws
 * @date 2020-08-06 11:58
 **/
@RequestMapping("/wechat")
@Controller
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class WechatAuthorizeController {

    private final WxMpService wxMpService;

    private final WechatConfig wechatConfig;

    private final StringRedisTemplate stringRedisTemplate;

    private final IWxUserService wxUserService;
    private final JwtHelper jwtHelper;
    private final MyUserDetailsService myUserDetailsService;
    private final AuthenticationManager authenticationManager;
    private final Environment environment;


    /**
     * 微信网页授权入口
     *
     * @param request
     * @param response
     * @return java.lang.String
     * @author sss
     * @date 2020/8/6 17:15
     **/
    @GetMapping("/authorize")
    public String authorize(HttpServletRequest request, HttpServletResponse response) {
        // 设置回调地址
        String url = wechatConfig.getRedireUrl() + "/wechat/userInfo";
        // 发起微信认证地址
        String redirect = "";
        redirect = wxMpService.oauth2buildAuthorizationUrl(
                url, WxConsts.OAuth2Scope.SNSAPI_USERINFO, wechatConfig.getState());
        log.info("请求地址等于:{}", redirect);
        return "redirect:" + redirect;
    }


    /**
     * 微信网页授权回调接口
     *
     * @param code      微信返回的编码
     * @param returnUrl 跳转的URL地址
     * @param response
     * @return java.lang.String
     * @author lws
     * @date 2020/8/6 17:16
     **/
    @GetMapping("/userInfo")
    public String getUserInfo(@RequestParam String code, @RequestParam("state") String returnUrl, HttpServletResponse response, HttpServletRequest request) throws IOException {
        log.info("自定义的state等于:{}", returnUrl);
        String accessToken = "";
        WxMpOAuth2AccessToken wxMpOAuth2AccessToken = null;
        try {
            wxMpOAuth2AccessToken = wxMpService.oauth2getAccessToken(code);
            WxMpUser wxMpUser = wxMpService.oauth2getUserInfo(wxMpOAuth2AccessToken, null);
            WxUser exitUser = wxUserService.getOne(new LambdaQueryWrapper<WxUser>().eq(WxUser::getOpenId, wxMpUser.getOpenId()));
            WxUser wxUser = new WxUser();
            if (exitUser == null) {
                wxUser.setOpenId(wxMpUser.getOpenId()).setAvatarUrl(wxMpUser.getHeadImgUrl()).setRoleId(2)
                        .setSubscribe(wxMpUser.getSubscribe()).setWxNickname(wxMpUser.getNickname());
                wxUserService.save(wxUser);
                exitUser = wxUser;
            } else {
                BeanUtils.copyProperties(exitUser, wxUser);
                wxUserService.update(new LambdaUpdateWrapper<WxUser>().set(WxUser::getAvatarUrl,wxMpUser.getHeadImgUrl()).eq(WxUser::getOpenId,wxMpUser.getOpenId()));
            }
            UserDetails details = null;
            if (!StringUtils.isEmpty(wxUser.getOpenId())) {
                details = myUserDetailsService.loadUserByUsername(wxUser.getOpenId());
            }
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            System.err.println("Subscribe等于：" + wxMpUser.getSubscribe());
            System.err.println("subscribeScene等于：" + wxMpUser.getSubscribeScene());
            log.info("用户信息等于：{},是否关注公众号：{}", wxMpUser, wxMpUser.getSubscribe());
            log.info("accessToken等于：{},scope等于：{}", wxMpOAuth2AccessToken.getAccessToken(), wxMpOAuth2AccessToken.getScope());
            log.info("wxMpOAuth2AccessToken等于：{}", wxMpOAuth2AccessToken);
            HashMap<String, Object> map = new HashMap<>(2);
            map.put("openId", wxMpUser.getOpenId());
            map.put("userId", wxUser.getId());
            accessToken = jwtHelper.generateToken(map);
        } catch (WxErrorException e) {
            e.printStackTrace();
        }
        return "redirect:" + wechatConfig.getReturnUrl() + "?" + "accessToken=" + accessToken;
//        return response.sendRedirect(returnUrl);
    }



    @GetMapping("/wx")
    @ResponseBody
    public String wxAuthorize(String signature, String timestamp, String nonce, String echostr) throws NoSuchAlgorithmException {
        System.out.println("singature等于：" + signature);
        System.out.println("timestamp等于：" + timestamp);
        System.out.println("nonce等于：" + nonce);
        System.out.println("echostr等于：" + echostr);
            String[] array = new String[]{"sssabc123456",timestamp,nonce};
        //String[] array = new String[]{"sss666", timestamp, nonce};
        Arrays.sort(array);
        String s = array[0] + array[1] + array[2];
        MessageDigest sha1 = MessageDigest.getInstance("sha1");
        byte[] digest = sha1.digest(s.getBytes());
        String message = sha1(digest);
        if (message.equalsIgnoreCase(signature)) {
            System.out.println("接入成功");
            return echostr;
        }
        System.out.println("接入失败");
        return null;
    }

    @PostMapping("/wx")
    @ResponseBody
    public String wxReceiveMessage(HttpServletRequest request,HttpServletResponse response) throws Exception {
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()){
            System.out.println("接收到的微信消息参数："+parameterNames.nextElement());
        }
        //校验信息
        WxMpXmlMessage inMessage = null;
        String encryptType = StringUtils.isBlank(request.getParameter("encrypt_type")) ?
                "raw" : request.getParameter("encrypt_type");
        if ("raw".equals(encryptType)) {
            // 明文传输的消息
            inMessage = WxMpXmlMessage.fromXml(request.getInputStream());
        } else if ("aes".equals(encryptType)) {
            // 是aes加密的消息
         //   String msgSignature = request.getParameter("msg_signature");
         //   inMessage = WxMpXmlMessage.fromEncryptedXml(request.getInputStream(), wxMpService.getWxMpConfigStorage(), timestamp, nonce, msgSignature);
        } else {
            response.getWriter().println("不可识别的加密类型");
            return null;
        }
        //配置路由
//        WxMpMessageRouter router = new WxMpMessageRouter(wxMpService);
//        router
//                .rule()
//                .msgType(WxConsts.XmlMsgType.TEXT)
//                .async(false)
//                .handler(wechatMessageUtil).end()
//
//                .rule()
//                .async(false)
//                .msgType(WxConsts.XmlMsgType.IMAGE)
//                .handler(wechatMessageUtil).end()
//
//                .rule()
//                .async(false)
//                .msgType(WxConsts.XmlMsgType.EVENT)
//                .event(WxConsts.EventType.SUBSCRIBE)
//                .handler(wechatMessageUtil).end()
//
//                .rule()
//                .async(false)
//                .msgType(WxConsts.XmlMsgType.EVENT)
//                .event(WxConsts.EventType.SCAN)
//                .handler(wechatMessageUtil).end()
//        ;
        Map<String, String> map = MessageUtil.parseXml(request);
        log.info("接收到消息:{}",map);
        return "ok";
    }

    private static String sha1(byte[] bytes) {
        final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5',
                '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        int len = bytes.length;
        StringBuilder buf = new StringBuilder(len * 2);
        // 把密文转换成十六进制的字符串形式
        for (int j = 0; j < len; j++) {
            buf.append(HEX_DIGITS[(bytes[j] >> 4) & 0x0f]);
            buf.append(HEX_DIGITS[bytes[j] & 0x0f]);
        }
        return buf.toString();
    }

    /**
     * 跳转接口文档地址
     *
     * @param response
     * @return java.lang.String
     * @author lws
     * @date 2020/11/4 9:43
    **/
    @GetMapping("/api/docs")
    public String apiDocs(HttpServletResponse response) throws IOException {
        return "/apidocs/index";
    }


    @GetMapping("/response/test")
    @ResponseBody
    public String response(String aa)  {
        return "返回包装参数测试";
    }

}
