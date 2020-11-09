package com.sss.sssforum.wxclient.user.dao;

import com.sss.sssforum.wxclient.dto.UserDTO;
import com.sss.sssforum.wxclient.user.entity.WxUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author sss
 * @since 2020-08-06
 */
public interface IWxUserDao extends BaseMapper<WxUser> {

    List<WxUser> getAllUserList(@Param("userName") String userName);
}
