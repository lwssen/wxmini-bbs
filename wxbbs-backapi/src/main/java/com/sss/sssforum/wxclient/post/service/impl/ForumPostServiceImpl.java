package com.sss.sssforum.wxclient.post.service.impl;

import com.github.pagehelper.PageHelper;
import com.sss.sssforum.config.properties.AliyunOSSProperties;
import com.sss.sssforum.config.properties.FileProperties;
import com.sss.sssforum.config.redisconfig.RedisUtil;
import com.sss.sssforum.constant.CommonConstant;
import com.sss.sssforum.enums.FileResponseEnum;
import com.sss.sssforum.enums.MessageType;
import com.sss.sssforum.exception.CommonException;
import com.sss.sssforum.utils.AliyunOssUtils;
import com.sss.sssforum.utils.MyFileUtils;
import com.sss.sssforum.utils.SecurityUtils;
import com.sss.sssforum.wxclient.dto.PostDTO;
import com.sss.sssforum.wxclient.dto.PostTypeDTO;
import com.sss.sssforum.wxclient.dto.UserMessageDTO;
import com.sss.sssforum.wxclient.file.dao.IFileInfoDao;
import com.sss.sssforum.wxclient.file.entity.FileInfo;
import com.sss.sssforum.wxclient.message.dao.IUserMessageDao;
import com.sss.sssforum.wxclient.message.entity.UserMessage;
import com.sss.sssforum.wxclient.message.service.IUserMessageService;
import com.sss.sssforum.wxclient.post.dao.IPostCommentDao;
import com.sss.sssforum.wxclient.post.entity.ForumPost;
import com.sss.sssforum.wxclient.post.dao.IForumPostDao;
import com.sss.sssforum.wxclient.post.entity.PostComment;
import com.sss.sssforum.wxclient.post.service.IForumPostService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sss.sssforum.wxclient.post.service.IPostCommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <p>
 * 所有帖子数据表 服务实现类
 * </p>
 *
 * @author sss
 * @since 2020-07-28
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class ForumPostServiceImpl extends ServiceImpl<IForumPostDao, ForumPost> implements IForumPostService {

    private final IForumPostDao forumPostDao;
    private final IUserMessageDao userMessageDao;
    private final IPostCommentDao postCommentDao;
    private final RedisUtil redisUtil;
    private final FileProperties fileProperties;
    private final Environment environment;
    private final IFileInfoDao fileInfoDao;
    private final AliyunOssUtils aliyunOssUtils;
    private final AliyunOSSProperties aliyunOSSProperties;
    private Object object=new Object();


    @Override
    public List<PostDTO> getPostList(Integer typeId,Boolean isDeleted,String title) {
        ForumPost forumPost = new ForumPost();
        forumPost.setTypesId(typeId).setIsDeleted(isDeleted);
        forumPost.setTitle(title);
        List<PostDTO> postList = forumPostDao.getPostList(forumPost);
        Integer userId = Integer.valueOf(SecurityUtils.getCurrentUserId() + "");
        if (!userId.equals(CommonConstant.ANONYMOUSUSER_INTEGER)){
            postList.forEach(k->{
                k.setIsLike(redisUtil.getBit(CommonConstant.LIKE_POST+k.getId(), userId));
            });
        }
        return postList;
    }

    @Override
    public Boolean increCommentcount(Long postId) {
        return forumPostDao.increCommentCount(postId) > 0;
    }

    @Override
    public Boolean increViewCount(Long postId) {
        return forumPostDao.increViewCount(postId) > 0;
    }

    @Override
    public   Integer increLikeCount(ForumPost forumPost) {
            forumPostDao.increLikeCount(forumPost.getId());
        ForumPost dbPost = getById(forumPost.getId());
        return dbPost.getLikeCount();
    }

    @Override
    public   Integer decreLikeCount(ForumPost forumPost) {
            forumPostDao.decreLikeCount(forumPost.getId());
        ForumPost dbPost = getById(forumPost.getId());
        return dbPost.getLikeCount();
    }

    @Override
    public Integer updatePostStatus(Long postId, Integer isDeleted) {
        return forumPostDao.updatePostStatus(postId, isDeleted);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer updatePost(PostTypeDTO postTypeDTO) {
        return forumPostDao.updatePost(postTypeDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserMessageDTO likePost(UserMessageDTO userMessageDTO) {
        Integer count =0;
        UserMessageDTO userMessageDTO1 = new UserMessageDTO();
        Long currentUserId = SecurityUtils.getCurrentUserId();
        Long getMessageUserId = userMessageDTO.getGetMessageUserId();
        Long postId = userMessageDTO.getPostId();
        synchronized (object){
            log.info("当前线程名称:{}",Thread.currentThread().getName());
            //帖子点赞数增加
            ForumPost forumPost = new ForumPost();
            forumPost.setId(userMessageDTO.getPostId());
            if (!redisUtil.getBit(CommonConstant.LIKE_POST+userMessageDTO.getPostId(),Integer.valueOf(SecurityUtils.getCurrentUserId()+""))) {
                //if (!userMessageDTO.getPublishUserId().equals(SecurityUtils.getCurrentUserId())) {
                    //往评论表插入数据
                    PostComment postComment = new PostComment();
                    postComment.setCommentContent("赞了赞我的帖子").setUserId(SecurityUtils.getCurrentUserId())
                            .setCommentType(MessageType.GOOD_LIKE.getCode()).setPostId(userMessageDTO.getPostId());
                    //查询数据
                    PostComment selectPostComment = new PostComment();
                    selectPostComment.setUserId(currentUserId).setCommentType(MessageType.GOOD_LIKE.getCode())
                            .setPostId(postId).setParentId(0);
                    PostComment selectOne = postCommentDao.myGetPostComment(selectPostComment);
                    if (selectOne !=null) {
                        selectOne.setIsDeleted(false).setUserId(currentUserId);
                        postCommentDao.updatePostCommentState(selectOne);
                    }else {
                        postCommentDao.insert(postComment);
                    }
                    //往消息表插入数据
                    UserMessage userMessage = new UserMessage();
                    userMessage.setMessageType(MessageType.GOOD_LIKE.getCode()).setPostId(userMessageDTO.getPostId())
                            .setUserId(userMessageDTO.getGetMessageUserId()).setPostCommentId(postComment.getId())
                            .setLikeCommentUserId(currentUserId);
                    //查询数据
                    boolean b = selectOne != null && selectOne.getId() != null;
                    Long selectOneId = b?selectOne.getId():0;
                    UserMessage selectUserMessage = new UserMessage();
                    selectUserMessage.setUserId(getMessageUserId).setLikeCommentUserId(currentUserId)
                            .setMessageType(MessageType.GOOD_LIKE.getCode()).setPostCommentId(selectOneId)
                            .setLikeCommentUserId(currentUserId).setPostId(postId);
                    UserMessage message = userMessageDao.myGetUserMessage(selectUserMessage);
                    if (message!=null){
                        message.setIsDeleted(false).setHasRead(0)
                                .setId(message.getId());
                        userMessageDao.updateUserMessageState(message);
                    }else {
                        userMessageDao.insert(userMessage);
                    }
               // }
                count = increLikeCount(forumPost);
                redisUtil.setBit(CommonConstant.LIKE_POST+userMessageDTO.getPostId(),SecurityUtils.getCurrentUserId(),true);
                userMessageDTO1.setLikeCount(count).setIsLike(true);
            }else {
                //取消点赞的数据
               // if (!userMessageDTO.getGetMessageUserId().equals(SecurityUtils.getCurrentUserId())) {
                    PostComment selectPostComment = new PostComment();
                    selectPostComment.setUserId(currentUserId).setCommentType(MessageType.GOOD_LIKE.getCode())
                            .setParentId(0).setPostId(postId);
                    PostComment cancelPostComment = postCommentDao.myGetPostComment(selectPostComment);
                    PostComment postComment = new PostComment();
                    postComment.setIsDeleted(true).setId(cancelPostComment.getId())
                            .setCommentType(MessageType.GOOD_LIKE.getCode());
                    postCommentDao.updatePostCommentState(postComment);
                    UserMessage userMessage = new UserMessage();
                    userMessage.setIsDeleted(true).setPostCommentId(cancelPostComment.getId())
                            .setUserId(getMessageUserId).setPostId(selectPostComment.getPostId());
                    userMessageDao.updateUserMessageState(userMessage);
              //  }
                count = decreLikeCount(forumPost);
                redisUtil.setBit(CommonConstant.LIKE_POST+userMessageDTO.getPostId(),SecurityUtils.getCurrentUserId(),false);
                userMessageDTO1.setLikeCount(count).setIsLike(false);
            }
        }
        return userMessageDTO1;
    }


    /**
     * 文件上传
     *
     * @param file  文件
     * @return java.lang.String
     * @author lws
     * @date 2020/8/17 15:43
    **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String uploadFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String fileSuffix = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        long size = file.getSize() /1000;
        String property = environment.getProperty("os.name");
        String savePath = fileProperties.getWindowsSavePath();
        if (StringUtils.isNotBlank(property) && property.equalsIgnoreCase("linux")) {
            savePath=fileProperties.getLinuxSavePath();
        }
        Long fileSize = fileProperties.getFileSize();
        String[] split = fileProperties.getFileSupportType().split(",");
        List<String> list = Arrays.asList(split);
        if (file.isEmpty()) {
            throw new CommonException(FileResponseEnum.FILE_EMPTY.getCode(),FileResponseEnum.FILE_EMPTY.getMessage());
        }
        if (size > fileSize) {
            throw new CommonException(FileResponseEnum.FILE_SIZE.getCode(),FileResponseEnum.FILE_SIZE.getMessage());
        }
        if (!list.contains(fileSuffix)){
            throw new CommonException(FileResponseEnum.FILE_TYPE.getCode(),FileResponseEnum.FILE_TYPE.getMessage());
        }
        String fileName = MyFileUtils.uploadFile(file, savePath);
        String resultPath=fileProperties.getServerAddress()+fileProperties.getRequestPath()+fileName ;
        FileInfo fileInfo = new FileInfo();
        fileInfo.setSavePath(savePath+fileName)
                .setFileName(fileName)
                .setRequestUrl(resultPath)
                .setSize(size);
        fileInfoDao.insert(fileInfo);
        return resultPath;
    }


    /**
     * 阿里云OSS文件上传
     *
     * @param file  文件
     * @return java.lang.String  {@link }
     * @author lws
     **/
    @Override
    public String ossUploadFile(MultipartFile file) {
        Optional.ofNullable(file).orElseThrow(()->new RuntimeException("文件为空"));
        String originalFilename = file.getOriginalFilename();
        String fileSuffix = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        long size = file.getSize() /1000;
        Long fileSize = fileProperties.getFileSize();
        String[] split = fileProperties.getFileSupportType().split(",");
        List<String> list = Arrays.asList(split);
        String savePath = aliyunOSSProperties.getFilePath();
        if (file.isEmpty()) {
            throw new CommonException(FileResponseEnum.FILE_EMPTY.getCode(),FileResponseEnum.FILE_EMPTY.getMessage());
        }
        if (size > fileSize) {
            throw new CommonException(FileResponseEnum.FILE_SIZE.getCode(),FileResponseEnum.FILE_SIZE.getMessage());
        }
        if (!list.contains(fileSuffix)){
            throw new CommonException(FileResponseEnum.FILE_TYPE.getCode(),FileResponseEnum.FILE_TYPE.getMessage());
        }
        FileInfo fileInfo  = aliyunOssUtils.ossUploadFile(file, aliyunOSSProperties.getSavePath(), null);
        fileInfo.setSize(size);
        fileInfoDao.insert(fileInfo);
        return fileInfo.getRequestUrl();

    }
}
