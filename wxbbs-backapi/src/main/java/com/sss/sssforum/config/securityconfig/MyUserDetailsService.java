package com.sss.sssforum.config.securityconfig;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sss.sssforum.wxclient.dto.MyUserDetails;
import com.sss.sssforum.wxclient.role.entity.WxRole;
import com.sss.sssforum.wxclient.role.service.IWxRoleService;
import com.sss.sssforum.wxclient.user.entity.WxUser;
import com.sss.sssforum.wxclient.user.service.IWxUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lws
 * @date 2020-08-06 20:09
 **/
@Component
public class MyUserDetailsService implements UserDetailsService {


    @Autowired
    private IWxUserService wxUserService;
    @Autowired
    private IWxRoleService roleService;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        WxUser wxUser = wxUserService.getOne(new LambdaQueryWrapper<WxUser>().eq(WxUser::getOpenId, userName));
        WxRole wxRole = roleService.getById(wxUser.getRoleId());
        MyUserDetails myUserDetails = new MyUserDetails();
        List<GrantedAuthority> authorityList = new ArrayList<>();
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(wxRole.getRoleName());
        authorityList.add(authority);
        myUserDetails.setWxUser(wxUser);
        myUserDetails.setRoles(authorityList);
        return myUserDetails ;
    }
}
