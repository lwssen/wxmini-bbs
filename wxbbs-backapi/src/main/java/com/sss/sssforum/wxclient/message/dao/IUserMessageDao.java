package com.sss.sssforum.wxclient.message.dao;

import com.sss.sssforum.wxclient.dto.UserMessageDTO;
import com.sss.sssforum.wxclient.message.entity.UserMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 用户消息表 Mapper 接口
 * </p>
 *
 * @author sss
 * @since 2020-08-04
 */
public interface IUserMessageDao extends BaseMapper<UserMessage> {


    List<UserMessageDTO> getUserMessageList(@Param("userMessage") UserMessage userMessage);

   Integer getMyMessageCount(@Param("userId") Long userId,@Param("messageType")Integer messageType);

   Integer updateUserMessageState(@Param("userMessage") UserMessage userMessage);

   UserMessage myGetUserMessage(UserMessage userMessage);
}
