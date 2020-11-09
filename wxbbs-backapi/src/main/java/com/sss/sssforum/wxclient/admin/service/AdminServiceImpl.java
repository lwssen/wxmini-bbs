package com.sss.sssforum.wxclient.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.sss.sssforum.config.redisconfig.RedisUtil;
import com.sss.sssforum.constant.CommonConstant;
import com.sss.sssforum.enums.MessageType;
import com.sss.sssforum.exception.CommonException;
import com.sss.sssforum.utils.SecurityUtils;
import com.sss.sssforum.wxclient.dto.PostCommentDTO;
import com.sss.sssforum.wxclient.message.entity.UserMessage;
import com.sss.sssforum.wxclient.message.service.IUserMessageService;
import com.sss.sssforum.wxclient.post.dao.IPostCommentDao;
import com.sss.sssforum.wxclient.post.entity.ForumPost;
import com.sss.sssforum.wxclient.post.entity.PostComment;
import com.sss.sssforum.wxclient.post.service.IForumPostService;
import com.sss.sssforum.wxclient.post.service.IPostCommentService;
import com.sss.sssforum.wxclient.post.service.IPostTypeService;
import com.sss.sssforum.wxclient.sensitiveword.service.ISensitiveWordService;
import com.sss.sssforum.wxclient.user.entity.WxUser;
import com.sss.sssforum.wxclient.user.service.IWxUserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author lws
 * @date 2020-09-08 08:43
 **/
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AdminServiceImpl implements IAdminService {

    private final IPostTypeService postTypeService;
    private  final IForumPostService forumPostService;
    private  final IWxUserService wxUserService;
    private  final IUserMessageService userMessageService;
    private  final IPostCommentService postCommentService;
    private  final RedisUtil redisUtil;
    private final ISensitiveWordService sensitiveWordService;
    private final IPostCommentDao postCommentDao;



    /**
     * 屏蔽评论内容
     *
     * @param postCommentDTO
     * @return boolean
     * @author lws
     * @date 2020/9/8 8:45
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String shieldingCommentContent(PostCommentDTO postCommentDTO) {
        String returnValue=postCommentDTO.getCommentContent();
        PostComment postComment = postCommentService.getById(postCommentDTO.getId());
        if (postComment == null) {
            throw  new CommonException(4004,"已删除，屏蔽失败");
        }

        PostComment comment = new PostComment();
        String oldCommentContent = postComment.getCommentContent();
        comment.setId(postCommentDTO.getId()).setOldCommentContent(oldCommentContent);
        if (StringUtils.isBlank(postCommentDTO.getCommentContent())) {
            comment.setCommentContent(CommonConstant.SHIELDING_COMMENT_CONTENT);
            returnValue=CommonConstant.SHIELDING_COMMENT_CONTENT;
        }
        if (StringUtils.isBlank(postComment.getOldCommentContent())) {
            //往评论表插入数据
            PostComment postComment2 = new PostComment();
            postComment2.setCommentContent(CommonConstant.SHIELDING_COMMENT_MESSAGE+postCommentDTO.getId()).setUserId(Long.valueOf(MessageType.NONEXISTENT.getCode()))
                    .setCommentType(MessageType.SYSTEM.getCode()).setPostId(postCommentDTO.getPostId());
            postCommentService.save(postComment2);
            //往消息表插入数据
            UserMessage userMessage = new UserMessage();
            userMessage.setUserId(postCommentDTO.getGetMessageUserId()).setPostId(postCommentDTO.getPostId())
                    .setMessageType(MessageType.SYSTEM.getCode())
                    .setPostCommentId(postComment2.getId());
            userMessageService.addMyMessage(userMessage);
            postCommentService.updateById(comment);
        }

        return returnValue;
    }


    /**
     * 评论内容置顶
     *
     * @param postCommentDTO
     * @return boolean
     * @author lws
     * @date 2020/9/8 8:46
     **/
    @Override
    public boolean setCommentContentTop(PostCommentDTO postCommentDTO) {
       Integer orderNumber = (Integer) postCommentService.getObj(new QueryWrapper<PostComment>().select("max(order_number) as orderNumber"), Function.identity());
        PostComment comment = new PostComment();
        if (Objects.isNull(postCommentDTO.getOrderNumber())) {
            comment.setId(postCommentDTO.getId()).setOrderNumber(orderNumber+1);
        }
        return postCommentService.updateById(comment);
    }


    /**
     * 管理帖子下的所有评论接口
     *
     * @param postId
     * @param commentContent
     * @return java.util.List<com.sss.sssforum.wxclient.dto.PostCommentDTO>  {@link }
     * @author lws
     **/
    @Override
    public List<PostCommentDTO> getAllPostCommentList(Long postId, String commentContent) {
        List<PostCommentDTO> postCommentList2=null;
        List<PostCommentDTO> postCommentList=null;
        Long currentUserId = SecurityUtils.getCurrentUserId();

        postCommentList2 = postCommentDao.getPostCommentList(postId,commentContent,0,true);
        postCommentList  = postCommentDao.getPostCommentList(postId,commentContent,null,true);

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
}
