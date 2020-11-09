package com.sss.sssforum.wxclient.dto;

import com.sss.sssforum.wxclient.user.entity.WxUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author lws
 * @date 2020-08-06 20:29
 **/
public class MyUserDetails implements UserDetails {
    private WxUser wxUser;

    private List<? extends GrantedAuthority> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles;
    }

    @Override
    public String getPassword() {
        return wxUser.getOpenId();
    }

    @Override
    public String getUsername() {
        return wxUser.getWxNickname();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public WxUser getWxUser() {
        return wxUser;
    }

    public void setWxUser(WxUser wxUser) {
        this.wxUser = wxUser;
    }

    public List<? extends GrantedAuthority> getRoles() {
        return roles;
    }

    public void setRoles(List<? extends GrantedAuthority> roles) {
        this.roles = roles;
    }
}
