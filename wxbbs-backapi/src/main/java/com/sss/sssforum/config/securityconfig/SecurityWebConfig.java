package com.sss.sssforum.config.securityconfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.Resource;

/**
 * @author lws
 * @date 2020-08-06 19:50
 **/
//@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true,securedEnabled = true)
public class SecurityWebConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private MyUserDetailsService myUserDetailsService;
    /**
     * Token验证处理器
     **/
    @Autowired
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    /**
     * 权限异常处理器
     **/
    @Resource
    private AccessDeniedHandler accessDeniedHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .and().sessionManagement()
                // 基于token，所以不需要session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                //允许匿名访问的路径
               // .authorizeRequests().antMatchers("/wehcat/**","/images/**","/wx/user/**","/wx/portal/**","/wehcat/api/**").permitAll()
                //需要登录后才能访问的路径
                .authorizeRequests()
                .antMatchers("/posts/push","/posts/like","/posts/image/upload",
                        "/post-comment","/post-comment/like",
                        "/my/**","/user-messages//count",
                        "/user-messages/type/count","/user-messages/clear")
                .authenticated().and()
               // 放行所有路径
               .authorizeRequests().antMatchers("/**").permitAll()
                .and().exceptionHandling().accessDeniedHandler(accessDeniedHandler)
                .and().csrf().disable().formLogin().disable()
                .authorizeRequests()
                .anyRequest().authenticated();
       // http.cors();
        // 添加JWT filter
        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
    }


    /**
     *  配置放行 URL路径
     *
     * @param web
     * @return void
     * @author lws
     * @date 2020/8/7 9:38
    **/
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(
                "/*.html",
                "/wechat/api/*.html",
                "/static/css/*.css",
                "/static/*.js",
                "/templates/*.html",
                "/**/*.html",
                "/**/*.css",
                "/**/*.js",
                "/wechat/**",
                "/wechat/api/*.css",
                "/wechat/*"

        ).regexMatchers(HttpMethod.GET,"/wechat/authorize,/wechat/test");
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    /**
     * 强散列哈希加密实现
     */
    @Bean
    public PasswordEncoder bCryptPasswordEncoder()
    {
        return NoOpPasswordEncoder.getInstance();
        //return new BCryptPasswordEncoder();
    }

    /**
     * 身份认证接口
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception
    {
        auth.userDetailsService(myUserDetailsService)
                .passwordEncoder(bCryptPasswordEncoder())
        ;
    }
}
