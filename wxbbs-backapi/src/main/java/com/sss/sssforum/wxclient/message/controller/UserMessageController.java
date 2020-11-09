package com.sss.sssforum.wxclient.message.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sss.sssforum.enums.MessageType;
import com.sss.sssforum.utils.Result;
import com.sss.sssforum.utils.SecurityUtils;
import com.sss.sssforum.wxclient.dto.UserMessageDTO;
import com.sss.sssforum.wxclient.message.entity.UserMessage;
import com.sss.sssforum.wxclient.message.service.IUserMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 用户消息接口列表
 * </p>
 *
 * @author sss
 * @since 2020-08-04
 */
@RestController
@RequestMapping("/user-messages")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserMessageController {

    private final IUserMessageService userMessageService;

    /**
     *  消息列表
     *
     * @param pageNum 页码数
     * @param pageSize 每页显示条数
     * @param messageType 消息类型
     * @return com.sss.sssforum.utils.Result<com.github.pagehelper.PageInfo>
     * @author lws
     * @date 2020/8/14 8:42
    **/
    @GetMapping
    public Result<PageInfo> useMessageList(@RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                           @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                                           @RequestParam Integer messageType) {
        return Result.succes(userMessageService.useMessageList(pageNum,pageSize,messageType));
    }


    /**
     * 获取未读消息总条数
     *
     * @return com.sss.sssforum.utils.Result
     * @author lws
     * @date 2020/8/5 17:11
    **/
    @GetMapping("/count")
    public Result getMessageCount(){
        int count = userMessageService.getMyMessageCount(null);
        return Result.succes(count);
    }


    /**
     * 获取各种未读的消息的条数
     *
     * @return com.sss.sssforum.utils.Result<com.sss.sssforum.wxclient.dto.UserMessageDTO>  {@link }
     * @author lws
    **/
    @GetMapping("/type/count")
    public Result<UserMessageDTO> getManyMessageTypeCount(){
        Integer messageCount=0;
        Integer likeCount=0;
        Integer notificationCount=0;
        List<UserMessage> messages = userMessageService.list(new QueryWrapper<UserMessage>().select("count(0) as typeTotal,message_type")
                .eq("user_id",SecurityUtils.getCurrentUserId()).eq("has_read",false).groupBy("message_type"));
        for (UserMessage message : messages) {
            if (message.getMessageType().equals(MessageType.OTHER_COMMENT.getCode()) ||message.getMessageType().equals(MessageType.REPLY_COMMENT.getCode()) ) {
                messageCount+=message.getTypeTotal();
            }else if (message.getMessageType().equals(MessageType.GOOD_LIKE.getCode())){
                likeCount+=message.getTypeTotal();
            }else if(message.getMessageType().equals(MessageType.SYSTEM.getCode())){
                notificationCount+=message.getTypeTotal();
            }

        }
        UserMessageDTO dto = UserMessageDTO.builder().likeCount(likeCount)
                .messageCount(messageCount).notificationCount(notificationCount).build();
        return Result.succes(dto);
    }

    /**
     * 清空未读消息
     *
     * @param messageType 消息类型
     * @return com.sss.sssforum.utils.Result  {@link }
     * @author lws
    **/
    @DeleteMapping("/clear")
    public Result clearMessageCount(@RequestParam Integer messageType){
       int count = userMessageService.getMyMessageCount(messageType);
       if (count >0) {
            boolean update = userMessageService.update(new LambdaUpdateWrapper<UserMessage>().eq(UserMessage::getUserId, SecurityUtils.getCurrentUserId()).setSql("has_read=1")
                    .eq(UserMessage::getMessageType,messageType)
                    .or().eq(MessageType.OTHER_COMMENT.getCode().equals(messageType),
                            UserMessage::getMessageType,MessageType.REPLY_COMMENT.getCode()));
            return Result.succes(update);
        }
        return Result.succes();
    }
}

