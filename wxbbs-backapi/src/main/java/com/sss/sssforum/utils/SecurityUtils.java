package com.sss.sssforum.utils;

import com.sss.sssforum.constant.CommonConstant;
import com.sss.sssforum.wxclient.dto.MyUserDetails;
import com.sss.sssforum.wxclient.user.entity.WxUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @author lws
 * @date 2020-08-07 09:49
 **/
public class SecurityUtils {

    /**
     * 获取Authentication
     */
    public static Authentication getAuthentication()
    {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 获取当前用户
     */
    public static WxUser getCurrentUser()
    {
        MyUserDetails myUserDetails= (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return myUserDetails.getWxUser();
    }

    /**
     * 获取当前用户ID
     */
    public static Long getCurrentUserId()
    {
        if(SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals("anonymousUser")){
            return CommonConstant.ANONYMOUSUSER_lONG;
        }

        MyUserDetails myUserDetails= (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return myUserDetails.getWxUser().getId();
    }
    /**
     * 生成BCryptPasswordEncoder密码
     *
     * @param password 密码
     * @return 加密字符串
     */
    public static String encryptPassword(String password)
    {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }



    /**
     * 判断密码是否相同
     *
     * @param rawPassword 真实密码
     * @param encodedPassword 加密后字符
     * @return 结果
     */
    public static boolean matchesPassword(String rawPassword, String encodedPassword)
    {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * 是否为管理员
     *
     * @param userId 用户ID
     * @return 结果
     */
    public static boolean isAdmin(Long userId)
    {
        return userId != null && 1L == userId;
    }

//    public static void main(String[] args) {
//        String s = encryptPassword("admin");
//        System.out.println(s);
//    }
}
