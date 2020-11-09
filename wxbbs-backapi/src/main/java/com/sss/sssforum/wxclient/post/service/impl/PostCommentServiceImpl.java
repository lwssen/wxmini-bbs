package com.sss.sssforum.wxclient.post.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.sss.sssforum.config.redisconfig.RedisUtil;
import com.sss.sssforum.config.springconfig.AsyncService;
import com.sss.sssforum.config.springconfig.ThreadPoolConfig;
import com.sss.sssforum.constant.CommonConstant;
import com.sss.sssforum.enums.MessageType;
import com.sss.sssforum.exception.CommonException;
import com.sss.sssforum.mail.ISendmailService;
import com.sss.sssforum.utils.MyDateTimeUtils;
import com.sss.sssforum.utils.SecurityUtils;
import com.sss.sssforum.utils.WxMiniContentCheckUtils;
import com.sss.sssforum.wxclient.dto.PostCommentDTO;
import com.sss.sssforum.wxclient.dto.UserMessageDTO;
import com.sss.sssforum.wxclient.message.dao.IUserMessageDao;
import com.sss.sssforum.wxclient.message.entity.UserMessage;
import com.sss.sssforum.wxclient.message.service.IUserMessageService;
import com.sss.sssforum.wxclient.post.dao.IForumPostDao;
import com.sss.sssforum.wxclient.post.entity.PostComment;
import com.sss.sssforum.wxclient.post.dao.IPostCommentDao;
import com.sss.sssforum.wxclient.post.service.IPostCommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sss.sssforum.wxclient.user.dao.IWxUserDao;
import com.sss.sssforum.wxclient.user.entity.WxUser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * <p>
 * 帖子评论表 服务实现类
 * </p>
 *
 * @author sss
 * @since 2020-08-03
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PostCommentServiceImpl extends ServiceImpl<IPostCommentDao, PostComment> implements IPostCommentService {

    private final IForumPostDao forumPostDao;
    private final IUserMessageDao userMessageDao;
    private final IPostCommentDao postCommentDao;
    private final RedisUtil  redisUtil;
    private final IWxUserDao wxUserDao;
    private final ThreadPoolConfig threadPoolConfig;
    private final ISendmailService sendmailService;



    @Override
    public Long pushComment(PostComment postComment) {
        WxMiniContentCheckUtils contentCheckUtils = new WxMiniContentCheckUtils();
       String accessToken = (String) redisUtil.get(CommonConstant.WX_TOKEN);
        threadPoolConfig.getExecutor().execute(()->{
            sendmailService.sendSimpleMail("新的评论发布","发布时间："+ MyDateTimeUtils.getCurrentDateTimeStr()+";--"+postComment.getCommentContent(),"986298329@qq.com","liuweisenss@163.com");
        });
        if (!contentCheckUtils.isPass(accessToken,postComment.getCommentContent())) {
            throw new CommonException(4001,"禁止发布敏感词");
        }
        //当前帖子的发布人ID
        Long publishUserId = postComment.getPublishUserId();
        //评论当前楼层的用户ID
        Long commentUserId = postComment.getUserId();
        postComment.setUserId(SecurityUtils.getCurrentUserId());
        save(postComment);
        //帖子评论数加 1
        forumPostDao.increCommentCount(postComment.getPostId());
        Long currentUserId = SecurityUtils.getCurrentUserId();
        //当前评论用户和发布人不是同一个人
     //   if (publishUserId!=null && !currentUserId.equals(publishUserId)) {
            UserMessage userMessage = new UserMessage();
            userMessage.setPostCommentId(postComment.getId()).setUserId(publishUserId).setPostId(postComment.getPostId());
            if(commentUserId !=null){
                Integer messageType = postComment.getMessageType()==null?1:postComment.getMessageType();
                userMessage.setMessageType(messageType).setUserId(commentUserId);
            }
            //往用户表插入未读消息类型
            userMessageDao.insert(userMessage);
      //  }
        return postComment.getId();
    }



    /**
     * 根据帖子ID查询出评论列表
     *
     * @param postId  帖子ID
     * @return java.util.List<com.sss.sssforum.wxclient.dto.PostCommentDTO>  {@link }
     * @author lws
     **/
    @Override
    public List<PostCommentDTO> getPostCommentList(Long postId,String commentContent ) {

         List<PostCommentDTO>  postCommentList2 = postCommentDao.getPostCommentList(postId, commentContent, 0, false);
         List<PostCommentDTO>  postCommentList = postCommentDao.getPostCommentList(postId, commentContent, null, false);

        getChildCommentList(postCommentList);
        postCommentList.removeIf(k -> k.getParentId()>0);
        for (PostCommentDTO dto : postCommentList) {
            if (CollectionUtils.isEmpty(dto.getChildCommentList())) {
                dto.setChildCommentList(new ArrayList<>());
            }
            dto.setManyCommentCount(dto.getChildCommentList().size());
            dto.setChildCommentList(null);
        }
        for (PostCommentDTO postCommentDTO : postCommentList2) {
            Long id = postCommentDTO.getId();
            for (PostCommentDTO commentDTO : postCommentList) {
                if (commentDTO.getId().equals(id)) {
                    postCommentDTO.setManyCommentCount(commentDTO.getManyCommentCount());
                    postCommentDTO.setIsLike(commentDTO.getIsLike());
                }
            }
        }
        return postCommentList2;
    }

    private void digui(List<PostCommentDTO> postCommentList,List<PostCommentDTO> childPostCommentList,PostCommentDTO onePostCommentDTO,List<PostCommentDTO> postCommentList2 ){
        for (PostCommentDTO postCommentDTO : postCommentList) {
            Long masterId = postCommentDTO.getId();
            List<PostCommentDTO> child = postCommentList2.stream().filter(k -> k.getParentId().equals(Integer.valueOf(masterId + ""))).collect(Collectors.toList());
            if (child.isEmpty()) {
                onePostCommentDTO.setChildCommentList(childPostCommentList);
                continue;
            }
            childPostCommentList.addAll(child);
            onePostCommentDTO.setChildCommentList(childPostCommentList);
            digui(child,childPostCommentList,onePostCommentDTO,postCommentList2);
            break;
        }
    }


    /**
     * 处理某一楼层的更多回复集合处理
     *
     * @param postCommentList
     * @return void
     * @author lws
     * @date 2020/8/19 14:12
    **/
    private void getChildCommentList(List<PostCommentDTO> postCommentList) {
        for (PostCommentDTO onePostCommentDTO : postCommentList) {
            Long masterId = onePostCommentDTO.getId();
            if (CommonConstant.ANONYMOUSUSER_INTEGER.equals(Integer.valueOf(SecurityUtils.getCurrentUserId() + ""))){
                onePostCommentDTO.setIsLike(false);
            }else {
                Boolean flag = redisUtil.getBit(CommonConstant.LIKE_POST_COMMENT + masterId, Integer.valueOf(SecurityUtils.getCurrentUserId() + ""));
                onePostCommentDTO.setIsLike(flag);
            }
            List<PostCommentDTO> childCommentList = onePostCommentDTO.getChildCommentList();
            if (childCommentList == null) {
                childCommentList = new ArrayList<>();
            }
            List<PostCommentDTO> child = postCommentList.stream().filter(k -> k.getParentId().equals(Integer.valueOf(masterId + ""))).collect(Collectors.toList());
            childCommentList.addAll(child);
            digui(child, childCommentList, onePostCommentDTO, postCommentList);
        }
    }


    /**
     *  某一楼层的更多回复的数据集合
     *
     * @param postCommentDTO
     * @return java.util.List<com.sss.sssforum.wxclient.dto.PostCommentDTO>
     * @author lws
     * @date 2020/8/19 14:20
    **/
    @Override
    public List<PostCommentDTO>  showManyComment(PostCommentDTO postCommentDTO){
        //查询出某一帖子的所有评论列表
        List<PostCommentDTO> postCommentList=null;
        Long currentUserId = SecurityUtils.getCurrentUserId();
        WxUser wxUser = wxUserDao.selectById(currentUserId);
        if (wxUser == null) {
            postCommentList = postCommentDao.getPostCommentList(postCommentDTO.getPostId(),null,null,false);

        }else {
            if (wxUser.getRoleId().equals(CommonConstant.ADMIN)) {
                postCommentList = postCommentDao.getPostCommentList(postCommentDTO.getPostId(), null, null, true);
            } else {
                postCommentList = postCommentDao.getPostCommentList(postCommentDTO.getPostId(), null, null, false);
            }
        }
        //要查看更多回复的当前楼层的回复ID
        Long currentPostCommentId = postCommentDTO.getId();
        getChildCommentList(postCommentList);
        PostCommentDTO postComment = postCommentList.stream().filter(k -> k.getId().equals(currentPostCommentId)).findFirst().orElse(new PostCommentDTO());
        Integer masterId =Integer.valueOf(postComment.getId()+"");
        List<PostCommentDTO> childCommentList = postComment.getChildCommentList();
        if (CollectionUtils.isEmpty(childCommentList)) {
            return new ArrayList<>();
        }
        childCommentList.sort(Comparator.comparing(PostComment::getCreateTime));
        for (PostCommentDTO commentDTO : childCommentList) {
            String childAnswerName = commentDTO.getWxUser().getWxNickname();
            String oldCommentContent =commentDTO.getCommentContent();
            Integer childMasterId =Integer.valueOf(commentDTO.getId()+"");
            if (commentDTO.getParentId().equals(masterId)) {
                String temp = commentDTO.getCommentContent();
                oldCommentContent = temp.contains(" //@")?commentDTO.getCommentContent().substring(0,temp.indexOf("@")-3):temp;
            }
            for (PostCommentDTO dto : childCommentList) {
                if (dto.getParentId().equals(childMasterId)) {
                    String temp=oldCommentContent;
                    oldCommentContent = temp.contains(" //@")?commentDTO.getCommentContent().substring(0,temp.indexOf("@")-3):temp;
                    String childContent = dto.getCommentContent();
                    String content = childContent + " //@" + childAnswerName + ": " +
                            oldCommentContent;
                    dto .setCommentContent(content);
                }
            }
        }
        childCommentList.forEach(k ->k.setChildCommentList(null));
        return childCommentList;

    }
    /**
     * 点赞他人评论或回复
     *
     * @param userMessageDTO
     * @return com.sss.sssforum.wxclient.dto.UserMessageDTO
     * @author lws
     * @date 2020/8/14 17:14
    **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserMessageDTO likePostComment(UserMessageDTO userMessageDTO) {
        Long postId = userMessageDTO.getPostId();
        Long getMessageUserId = userMessageDTO.getGetMessageUserId();
        Long postCommentId = userMessageDTO.getPostCommentId();
        Long currentUserId = SecurityUtils.getCurrentUserId();
        Integer likeCommentCount=0;
        Boolean isLike=false;
        if (!redisUtil.getBit(CommonConstant.LIKE_POST_COMMENT+postCommentId,Integer.valueOf(SecurityUtils.getCurrentUserId()+""))){
            isLike=true;
            //if (!userMessageDTO.getGetMessageUserId().equals(SecurityUtils.getCurrentUserId())) {
                //往评论表插入数据
                PostComment insertPostComment = new PostComment();
                insertPostComment.setCommentContent("赞了赞你的回复").setUserId(SecurityUtils.getCurrentUserId())
                        .setCommentType(MessageType.GOOD_LIKE.getCode()).setPostId(postId).setParentId(Integer.valueOf(postCommentId+""));
                //查询数据
                PostComment selectPostComment = new PostComment();
                selectPostComment.setUserId(currentUserId).setCommentType(MessageType.GOOD_LIKE.getCode())
                        .setParentId(Integer.valueOf(postCommentId+"")).setPostId(postId);
                PostComment selectOne = postCommentDao.myGetPostComment(selectPostComment);
                if (selectOne !=null) {
                    selectOne.setIsDeleted(false).setUserId(currentUserId);
                    postCommentDao.updatePostCommentState(selectOne);
                }else {
                    postCommentDao.insert(insertPostComment);
                }
                //往消息表插入数据
                UserMessage userMessage = new UserMessage();
                boolean b = selectOne != null && selectOne.getId() != null;
                Long selectOneId = b?selectOne.getId():0;
                userMessage.setMessageType(MessageType.GOOD_LIKE.getCode()).setPostId(userMessageDTO.getPostId())
                        .setUserId(getMessageUserId).setPostCommentId(insertPostComment.getId()).setLikeCommentUserId(currentUserId);
                //查询数据
                UserMessage selectUserMessage = new UserMessage();
                selectUserMessage.setUserId(getMessageUserId).setLikeCommentUserId(currentUserId)
                        .setMessageType(MessageType.GOOD_LIKE.getCode()).setPostCommentId(selectOneId);
                UserMessage message = userMessageDao.myGetUserMessage(selectUserMessage);
                if (message!=null){
                    message.setIsDeleted(false).setHasRead(0)
                            .setId(message.getId());
                    userMessageDao.updateUserMessageState(message);
                }else {
                    userMessageDao.insert(userMessage);
                }
           // }
            redisUtil.setBit(CommonConstant.LIKE_POST_COMMENT+postCommentId,SecurityUtils.getCurrentUserId(),true);
            postCommentDao.incrLikePostComment(postCommentId);
        }else {
            //取消点赞的数据
           // if (!userMessageDTO.getGetMessageUserId().equals(SecurityUtils.getCurrentUserId())) {
                PostComment selectPostComment = new PostComment();
                selectPostComment.setUserId(currentUserId).setCommentType(MessageType.GOOD_LIKE.getCode())
                        .setParentId(Integer.valueOf(postCommentId + "")).setPostId(postId);
                PostComment cancelPostComment = postCommentDao.myGetPostComment(selectPostComment);
                PostComment postComment = new PostComment();
                postComment.setIsDeleted(true).setId(cancelPostComment.getId()).setCommentType(MessageType.GOOD_LIKE.getCode());
                postCommentDao.updatePostCommentState(postComment);
                UserMessage userMessage = new UserMessage();
                userMessage.setIsDeleted(true).setPostCommentId(cancelPostComment.getId()).setUserId(getMessageUserId);
                userMessageDao.updateUserMessageState(userMessage);
          //  }
            postCommentDao.decrLikePostComment(postCommentId);
            redisUtil.setBit(CommonConstant.LIKE_POST_COMMENT+postCommentId,SecurityUtils.getCurrentUserId(),false);
        }
        PostComment postComment = getById(postCommentId);
        likeCommentCount=postComment.getCommentLikeCount();
        UserMessageDTO messageDTO = new UserMessageDTO();
        messageDTO.setIsLike(isLike).setLikeCount(likeCommentCount);
        return messageDTO;
    }
}
