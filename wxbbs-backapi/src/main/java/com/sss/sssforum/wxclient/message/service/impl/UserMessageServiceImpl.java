package com.sss.sssforum.wxclient.message.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sss.sssforum.constant.CommonConstant;
import com.sss.sssforum.enums.MessageType;
import com.sss.sssforum.utils.SecurityUtils;
import com.sss.sssforum.wxclient.dto.UserMessageDTO;
import com.sss.sssforum.wxclient.message.entity.UserMessage;
import com.sss.sssforum.wxclient.message.dao.IUserMessageDao;
import com.sss.sssforum.wxclient.message.service.IUserMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sss.sssforum.wxclient.post.dao.IPostCommentDao;
import com.sss.sssforum.wxclient.post.dao.IPostTypeDao;
import com.sss.sssforum.wxclient.post.entity.PostComment;
import com.sss.sssforum.wxclient.user.dao.IWxUserDao;
import com.sss.sssforum.wxclient.user.entity.WxUser;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户消息表 服务实现类
 * </p>
 *
 * @author sss
 * @since 2020-08-04
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserMessageServiceImpl extends ServiceImpl<IUserMessageDao, UserMessage> implements IUserMessageService {
    private final IUserMessageDao userMessageDao;
    private final IPostTypeDao postTypeDao;
    private final IPostCommentDao postCommentDao;
    private final IWxUserDao wxUserDao;

    /**
     *  用户消息
     *
     * @param pageNum 页码数
     * @param pageSize 每页条数
     * @return com.github.pagehelper.PageInfo
     * @author lws
     * @date 2020/8/4 10:47
    **/
    @Override
    public PageInfo useMessageList(Integer pageNum, Integer pageSize,Integer messageType) {
        UserMessage userMessage = new UserMessage();
        userMessage.setMessageType(messageType).setUserId(SecurityUtils.getCurrentUserId());
        PageHelper.startPage(pageNum, pageSize);
        List<UserMessageDTO> list = userMessageDao.getUserMessageList(userMessage);
        concatMessageContent(list);
        PageInfo<UserMessageDTO> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }

    /**
     * 消息内容拼接处理
     *
     * @param list 消息集合
     * @return void
     * @author lws
     * @date 2020/8/19 10:03
    **/
    private void concatMessageContent(List<UserMessageDTO> list) {
        for (UserMessageDTO messageDTO : list) {
            //评论或回复通知
            if (MessageType.OTHER_COMMENT.getCode().equals(messageDTO.getMessageType())) {
                messageDTO.setMessageContent(messageDTO.getTitle());
            }else if(MessageType.REPLY_COMMENT.getCode().equals(messageDTO.getMessageType())){
               // PostComment postComment = postCommentDao.selectById(messageDTO.getParentId());
                PostComment postComment = postCommentDao.selectIsDeletedById(messageDTO.getParentId());
                WxUser wxUser = wxUserDao.selectById(postComment.getUserId());
                String commentContent = messageDTO.getCommentContent();
                messageDTO.setCommentContent("回复 //@"+wxUser.getWxNickname()+":"+commentContent);
                messageDTO.setMessageContent("我："+seccutContent(postComment.getCommentContent()));
            }else if (MessageType.GOOD_LIKE.getCode().equals(messageDTO.getMessageType())){
                //点赞通知
                messageDTO.setMessageContent(messageDTO.getTitle());
                if (messageDTO.getParentId() > 0) {
                    PostComment postComment = new PostComment();
                    postComment.setId(messageDTO.getParentId());
                    PostComment comment = postCommentDao.myGetPostComment(postComment);
                    if (comment !=null) {
                        messageDTO.setMessageContent("我: "+seccutContent(comment.getCommentContent()));
                    }
                }
            } else {
                //系统通知
                if (messageDTO.getCommentContent().equals(CommonConstant.SYSTEM_MESSAGE)) {
                    messageDTO.setMessageContent(messageDTO.getTitle()).setCommentContent(CommonConstant.SYSTEM_MESSAGE_TEMPLATE);
                }else if (messageDTO.getCommentContent().startsWith(CommonConstant.MOVE_POST_MESSAGE)){
                    String commentContent = messageDTO.getCommentContent();
                    String typeId = commentContent.substring(commentContent.indexOf("@")+1);
                    String messageContent = commentContent.substring(0,commentContent.indexOf(",")+1);
                    String typeName = postTypeDao.selectById(Long.valueOf(typeId)).getTypeName();
                    String content=messageContent+"您的帖子已被转移到 "+typeName+" 分类";
                    messageDTO.setMessageContent(messageDTO.getTitle()).setCommentContent(seccutContent(content));
                }else if (messageDTO.getCommentContent().startsWith(CommonConstant.DELETE_COMMENT_MESSAGE)){
                    String commentContent = messageDTO.getCommentContent();
                    String postCommentId = commentContent.substring(commentContent.indexOf("@")+1);
                    PostComment postComment = postCommentDao.selectIsDeletedById(Long.valueOf(postCommentId));
                    messageDTO.setCommentContent(CommonConstant.DELETE_COMMENT_MESSAGE_TEMPLATE);
                    messageDTO.setMessageContent(seccutContent(postComment.getCommentContent()));
                }else if (messageDTO.getCommentContent().startsWith(CommonConstant.SHIELDING_COMMENT_MESSAGE)){
                    String commentContent = messageDTO.getCommentContent();
                    String postCommentId = commentContent.substring(commentContent.indexOf("@")+1);
                    PostComment postComment = postCommentDao.selectIsDeletedById(Long.valueOf(postCommentId));
                    messageDTO.setCommentContent(CommonConstant.SHIELDING_COMMENT_MESSAGE_TEMPLATE);
                    messageDTO.setMessageContent(seccutContent(postComment.getOldCommentContent()));
                }
            }
        }
    }

    @Override
    public Integer getMyMessageCount(Integer messageType) {
        return userMessageDao.getMyMessageCount(SecurityUtils.getCurrentUserId(),messageType);
    }

    @Override
    public Boolean addMyMessage(UserMessage userMessage) {
        return save(userMessage);
    }


    /**
     * 评论内容字符串截取
     *
     * @param content 评论内容或回复
     * @return java.lang.String
     * @author lws
     * @date 2020/8/19 9:58
    **/
    private String seccutContent(String content){
        String result=null;
        if (StringUtils.isBlank(content) || content.trim().length() < 30) {
            return content;
        }
        if (content.trim().length() > 30) {
            result = content.trim().substring(0, 30)+"...";
        }
        return result;
    }
}
