package com.sss.sssforum.wxclient.message.service;

import com.github.pagehelper.PageInfo;
import com.sss.sssforum.wxclient.message.entity.UserMessage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * <p>
 * 用户消息表 服务类
 * </p>
 *
 * @author sss
 * @since 2020-08-04
 */
public interface IUserMessageService extends IService<UserMessage> {

    /**
     * 我的消息列表
     *
     * @param pageNum
     * @param pageSize
     * @param messageType
     * @return
     */
    PageInfo useMessageList(Integer pageNum, Integer pageSize,Integer messageType);

    /**
     * 获取我的未读消息条数
     * @return
     */
    Integer getMyMessageCount(Integer messageType);

    /**
     * 新增我的未读消息
     * @param userMessage
     * @return
     */
    Boolean addMyMessage(UserMessage userMessage);
}
