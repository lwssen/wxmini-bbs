package com.sss.sssforum.wxclient.user.service;

import com.sss.sssforum.wxclient.dto.PostDTO;
import com.sss.sssforum.wxclient.dto.UserDTO;
import com.sss.sssforum.wxclient.user.entity.WxUser;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author sss
 * @since 2020-08-06
 */
public interface IWxUserService extends IService<WxUser> {

    List<PostDTO> myPostList(String title);

    List<WxUser> getAllUserList(String userName);

    void test();

    void testA();

    void testB();

    void testException();
}
