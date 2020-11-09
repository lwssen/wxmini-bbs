package com.sss.sssforum.wxclient.wechat.miniapp;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.sss.sssforum.config.securityconfig.MyUserDetailsService;
import com.sss.sssforum.config.springconfig.JwtHelper;
import com.sss.sssforum.config.wxconfig.WechatConfig;
import com.sss.sssforum.config.wxconfig.WxminiappConfig;
import com.sss.sssforum.utils.JsonUtils;
import com.sss.sssforum.utils.Result;
import com.sss.sssforum.wxclient.user.entity.WxUser;
import com.sss.sssforum.wxclient.user.service.IWxUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

/**
 * 微信小程序获取用户信息接口列表
 *
 * @author lws
 * @date 2020-09-10 09:02
 **/
@RestController
@RequestMapping("/wx/user")
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WxMiniappUserController {

    private final WxMpService wxMpService;

    private final WechatConfig wechatConfig;

    private final StringRedisTemplate stringRedisTemplate;

    private final IWxUserService wxUserService;
    private final JwtHelper jwtHelper;
    private final MyUserDetailsService myUserDetailsService;
    private final AuthenticationManager authenticationManager;
    private final Environment environment;
    /**
     * 登陆接口(获取微信用户的openId)
     */
    @GetMapping("/{appid}/login")
    public Result login(@PathVariable String appid, String code, HttpServletRequest request) {
        if (StringUtils.isBlank(code)) {
            return Result.fail("empty jscode");
        }

        final WxMaService wxService = WxminiappConfig.getMaService(appid);
       // String accessToken = wxService.getAccessToken();
        try {
            WxMaJscode2SessionResult session = wxService.getUserService().getSessionInfo(code);
            log.info(session.getSessionKey());
            log.info("用户的openId等于：{}",session.getOpenid());
            //TODO 可以增加自己的逻辑，关联业务相关数据
            return Result.succes(JsonUtils.toJson(session));
        } catch (WxErrorException e) {
            log.error(e.getMessage(), e);
            return Result.fail();
        }
    }

    /**
     * 保存用户信息
     *
     * @param requestWxUser 用户信息
     * @return com.sss.sssforum.utils.Result
     * @author lws
     * @date 2020/9/10 10:45
    **/
    @PostMapping("/save")
    public Result saveUserInfo(@RequestBody  WxUser requestWxUser, HttpServletRequest request){
        String errorMessage;
        try {
            WxUser exitUser = wxUserService.getOne(new LambdaQueryWrapper<WxUser>().eq(WxUser::getOpenId, requestWxUser.getOpenId()));
            WxUser wxUser = new WxUser();
            if (exitUser == null) {
                wxUser.setOpenId(requestWxUser.getOpenId()).setAvatarUrl(requestWxUser.getAvatarUrl()).setRoleId(2)
                        .setSubscribe(false).setWxNickname(requestWxUser.getWxNickname());
                wxUserService.save(wxUser);
                exitUser = wxUser;
            } else {
                BeanUtils.copyProperties(exitUser, wxUser);
                wxUserService.update(new LambdaUpdateWrapper<WxUser>().set(WxUser::getAvatarUrl, requestWxUser.getAvatarUrl())
                        .eq(WxUser::getOpenId, requestWxUser.getOpenId()));
            }
            UserDetails details = null;
            if (!StringUtils.isEmpty(wxUser.getOpenId())) {
                details = myUserDetailsService.loadUserByUsername(wxUser.getOpenId());
            }
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            HashMap<String, Object> map = new HashMap<>(2);
            map.put("openId", wxUser.getOpenId());
            map.put("userId", wxUser.getId());
            String accessToken = jwtHelper.generateToken(map);
            return Result.succes(accessToken);
        } catch (Exception e) {
            errorMessage=e.getMessage();
        }
        return Result.fail(errorMessage);
    }

    @PostMapping("/get/token")
    public Result getAcessToken(@RequestBody  WxUser requestWxUser, HttpServletRequest request){
        String errorMessage;
        try {
            WxUser wxUser = wxUserService.getOne(new LambdaQueryWrapper<WxUser>().eq(WxUser::getOpenId, requestWxUser.getOpenId()));
            if (wxUser == null) {
                return Result.fail("用户不存在,获取Token失败");
            }
            UserDetails details = null;
            if (!StringUtils.isEmpty(wxUser.getOpenId())) {
                details = myUserDetailsService.loadUserByUsername(wxUser.getOpenId());
            }
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            HashMap<String, Object> map = new HashMap<>(2);
            map.put("openId", wxUser.getOpenId());
            map.put("userId", wxUser.getId());
            String accessToken = jwtHelper.generateToken(map);
            return Result.succes(accessToken);
        } catch (Exception e) {
            errorMessage=e.getMessage();
        }
        return Result.fail(errorMessage);
    }
    /**
     * <pre>
     * 获取用户信息接口
     * </pre>
     */
    @GetMapping("/{appid}/info")
    public String info(@PathVariable String appid, String sessionKey,
                       String signature, String rawData, String encryptedData, String iv) {
        final WxMaService wxService = WxminiappConfig.getMaService(appid);

        // 用户信息校验
        if (!wxService.getUserService().checkUserInfo(sessionKey, rawData, signature)) {
            return "user check failed";
        }

        // 解密用户信息
        WxMaUserInfo userInfo = wxService.getUserService().getUserInfo(sessionKey, encryptedData, iv);

        return JsonUtils.toJson(userInfo);
    }

    /**
     * <pre>
     * 获取用户绑定手机号信息
     * </pre>
     */
    @GetMapping("/{appid}/phone")
    public String phone(@PathVariable String appid, String sessionKey, String signature,
                        String rawData, String encryptedData, String iv) {
        final WxMaService wxService = WxminiappConfig.getMaService(appid);

        // 用户信息校验
        if (!wxService.getUserService().checkUserInfo(sessionKey, rawData, signature)) {
            return "user check failed";
        }

        // 解密
        WxMaPhoneNumberInfo phoneNoInfo = wxService.getUserService().getPhoneNoInfo(sessionKey, encryptedData, iv);

        return JsonUtils.toJson(phoneNoInfo);
    }
}
