package com.sss.sssforum.wxclient.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.pagehelper.PageInfo;
import com.sss.sssforum.annotation.SensitiveWordCheck;
import com.sss.sssforum.base.BaseController;
import com.sss.sssforum.config.redisconfig.RedisUtil;
import com.sss.sssforum.constant.CommonConstant;
import com.sss.sssforum.enums.MessageType;
import com.sss.sssforum.utils.Result;
import com.sss.sssforum.utils.SecurityUtils;
import com.sss.sssforum.wxclient.admin.service.IAdminService;
import com.sss.sssforum.wxclient.dto.*;
import com.sss.sssforum.wxclient.message.entity.UserMessage;
import com.sss.sssforum.wxclient.message.service.IUserMessageService;
import com.sss.sssforum.wxclient.post.entity.ForumPost;
import com.sss.sssforum.wxclient.post.entity.PostComment;
import com.sss.sssforum.wxclient.post.entity.PostType;
import com.sss.sssforum.wxclient.post.service.IForumPostService;
import com.sss.sssforum.wxclient.post.service.IPostCommentService;
import com.sss.sssforum.wxclient.post.service.IPostTypeService;
import com.sss.sssforum.wxclient.sensitiveword.entity.SensitiveWord;
import com.sss.sssforum.wxclient.sensitiveword.service.ISensitiveWordService;
import com.sss.sssforum.wxclient.user.entity.WxUser;
import com.sss.sssforum.wxclient.user.service.IWxUserService;
import io.github.yedaxia.apidocs.Ignore;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 管理员接口列表
 *
 * @author lws
 * @date 2020-08-09 18:15
 **/
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/system")
public class AdminContrloller extends BaseController {

    private final IPostTypeService postTypeService;
    private  final IForumPostService forumPostService;
    private  final IWxUserService wxUserService;
    private  final IUserMessageService userMessageService;
    private  final IPostCommentService postCommentService;
    private  final RedisUtil redisUtil;
    private final ISensitiveWordService sensitiveWordService;
    private final IAdminService adminService;


    /**
     * 转移帖子类型
     *
     * @description 管理员
     * @param postTypeDTO
     * @return com.sss.sssforum.utils.Result  {@link }
     * @author lws
    **/
    @PutMapping("/change/post-type")
    @PreAuthorize("hasAuthority('wxAdmin')")
    public Result changePostType(@RequestBody PostTypeDTO postTypeDTO){
        if (!postTypeDTO.getIsDeleted()) {
            //往评论表插入数据
            PostComment postComment = new PostComment();
            postComment.setCommentContent(CommonConstant.MOVE_POST_MESSAGE + postTypeDTO.getTypesId())
                    .setCommentType(MessageType.SYSTEM.getCode()).setPostId(postTypeDTO.getPostId());
            Long systemUserId = Long.valueOf(MessageType.NONEXISTENT.getCode());
            postComment.setUserId(systemUserId);
            postCommentService.save(postComment);
            //往消息表插入数据
            UserMessage userMessage = new UserMessage();
            userMessage.setUserId(postTypeDTO.getGetMessageUserId()).setPostId(postTypeDTO.getPostId())
                    .setMessageType(MessageType.SYSTEM.getCode())
                    .setPostCommentId(postComment.getId());
            userMessageService.addMyMessage(userMessage);
        }
//        ForumPost forumPost = new ForumPost();
//        forumPost.setId(postTypeDTO.getPostId()).setTypesId(postTypeDTO.getTypesId());
//        forumPostService.updateById(forumPost);
          forumPostService.updatePost(postTypeDTO);
        return Result.succes();
    }

    /**
     * 是否删除或恢复帖子
     *
     * @param postTypeDTO
     * @return com.sss.sssforum.utils.Result  {@link }
     * @author lws
    **/
    @PutMapping("/delete/post")
    @PreAuthorize("hasAuthority('wxAdmin')")
    public Result delPost(@RequestBody PostTypeDTO postTypeDTO){
        Integer isDeleted=0;
        if (postTypeDTO.getIsDeleted()) {
            isDeleted=1;
            //往评论表插入数据
            PostComment postComment = new PostComment();
            postComment.setCommentContent(CommonConstant.SYSTEM_MESSAGE).setUserId(Long.valueOf(MessageType.NONEXISTENT.getCode()))
                    .setCommentType(MessageType.SYSTEM.getCode()).setPostId(postTypeDTO.getPostId());
            postCommentService.save(postComment);
            //往消息表插入数据
            UserMessage userMessage = new UserMessage();
            userMessage.setUserId(postTypeDTO.getGetMessageUserId()).setPostId(postTypeDTO.getPostId())
                    .setMessageType(MessageType.SYSTEM.getCode())
                    .setPostCommentId(postComment.getId());
            userMessageService.addMyMessage(userMessage);

        }
        forumPostService.updatePostStatus(postTypeDTO.getPostId(),isDeleted);
        return Result.succes();
    }

    /**
     * 查询所有帖子列表
     * @param typeId 类型ID
     * @return com.sss.sssforum.utils.Result<PageInfo<PostDTO>>  {@link PostDTO}
     * @author lws
    **/
    @GetMapping("/all/posts")
    @PreAuthorize("hasAuthority('wxAdmin')")
    public Result<PageInfo<PostDTO>> getAllPostList(@RequestParam(required = false)  Integer typeId,
                                                    @RequestParam(required = false)  String title,
                                                    @RequestParam(required = false)  String orderValue){
        if (StringUtils.isNotBlank(orderValue)) {
            startPage("fp."+orderValue+" desc,fp.create_time","desc");
        }else {
            startPage("fp.create_time","desc");
        }
        List<PostDTO> list = forumPostService.getPostList(typeId,false,title);
        PageInfo<PostDTO> pageInfo = new PageInfo<>(list);
        return Result.succes(pageInfo);
    }


    /**
     * 根据帖子ID查询评论列表
     *
     * @param postId  帖子ID
     * @return com.sss.sssforum.utils.Result<java.util.List<com.sss.sssforum.wxclient.post.entity.PostComment>>  {@link }
     * @author lws
     **/
    @GetMapping("/admin/list/{postId}")
    @PreAuthorize("hasAuthority('wxAdmin')")
    public Result<PageInfo<PostCommentDTO>> getPostCommentList(@PathVariable Long postId,@RequestParam(required = false) String commentContent ){
        startPage();
        List<PostCommentDTO> postComments = adminService.getAllPostCommentList(postId,commentContent);
        PageInfo<PostCommentDTO> pageInfo = new PageInfo<>(postComments);
        return Result.succes(pageInfo);
    }
    /**
     * 查询所有用户
     *
     * @param userName 用户名称
     * @return com.sss.sssforum.utils.Result<com.github.pagehelper.PageInfo>
     * @author lws
     * @date 2020/8/13 14:49
    **/
    @GetMapping("/all/users")
    @PreAuthorize("hasAuthority('wxAdmin')")
    public Result<PageInfo> getAllUsers(String userName){
        startPage();
        List<WxUser> allUserList = wxUserService.getAllUserList(userName);
        PageInfo<WxUser> pageInfo = new PageInfo<>(allUserList);
        return Result.succes(pageInfo);
    }

    /**
     * 类型列表
     *
     * @param postType
     * @return com.sss.sssforum.utils.Result<java.lang.Boolean>
     * @author lws
     * @date 2020/8/13 14:49
    **/
    @PutMapping("/post-types")
    @PreAuthorize("hasAuthority('wxAdmin')")
    public Result<Boolean> changePostType(@RequestBody PostType postType){
       return Result.succes(postTypeService.saveOrUpdate(postType));
    }


    /**
     * 删除类型
     *
     * @param typeId 类型ID
     * @return com.sss.sssforum.utils.Result<java.lang.Boolean>
     * @author lws
     * @date 2020/8/13 14:50
    **/
    @DeleteMapping("/post-types/{typeId}")
    @PreAuthorize("hasAuthority('wxAdmin')")
    public Result<Boolean> changePostType(@PathVariable Long typeId){
       return Result.succes(postTypeService.removeById(typeId));
    }


    /**
     * 查询总用户数和总帖子数
     *
     * @return com.sss.sssforum.utils.Result<com.sss.sssforum.wxclient.dto.UserDTO>
     * @author lws
     * @date 2020/8/13 14:50
    **/
    @Ignore
    @GetMapping("/total/counts")
    @PreAuthorize("hasAuthority('wxAdmin')")
    public  Result<UserDTO> getTotalCount(){
        int userTotalCount = wxUserService.count();
        int postTotalCount = forumPostService.count();
        UserDTO userDTO = new UserDTO().setPostTotalCount(postTotalCount).setUserTotalCount(userTotalCount);
        return Result.succes(userDTO);
    }

    
    /**
     * 获取敏感词列表
     * 
     * 
     * @param sensitiveWordDTO
     * @param pageNum
     * @param pageSize 
     * @return com.sss.sssforum.utils.Result  {@link }
     * @author lws
    **/
    @GetMapping("/sensitiveWords")
    @PreAuthorize("hasAuthority('wxAdmin')")
    public Result getSensitiveWordList(SensitiveWordDTO sensitiveWordDTO,Integer pageNum ,Integer pageSize){
        boolean hasNextPage = false;
        Long size = redisUtil.lLength(CommonConstant.SENSITIVEWORD_LIST);
        Integer start = (pageNum - 1) * pageSize;
        Integer end = (start + pageSize) - 1;
        long temp = size % pageSize;
        if ( size < pageSize) {
            temp = 0;
        }
        long totalNum = temp > 0 ? temp + 1 : temp;
        List<SensitiveWordDTO> sensitiveWordList = new ArrayList<>();
        PageInfo<SensitiveWordDTO> pageInfo = new PageInfo<>(sensitiveWordList);

        List<String> list = null;
        if (pageNum == totalNum || totalNum == 0) {
            list = (List<String>) redisUtil.lRange(CommonConstant.SENSITIVEWORD_LIST, 0, -1);
        } else {
            hasNextPage = true;
            list = (List<String>) redisUtil.lRange(CommonConstant.SENSITIVEWORD_LIST, start, end);
        }
        for (int i = 0; i < list.size(); i++) {
            SensitiveWordDTO build = SensitiveWordDTO.builder().index(Long.valueOf(i + ""))
                    .sensitiveWord(list.get(i)).build();
            sensitiveWordList.add(build);
        }
        pageInfo.setList(sensitiveWordList);
        pageInfo.setHasNextPage(hasNextPage);
        pageInfo.setTotal(size);
        pageInfo.setPageNum(Integer.valueOf(totalNum + ""));
        return Result.succes(pageInfo);
    }

    /**
     * 敏感词增加
     *
     * @param sensitiveWordDTO
     * @return com.sss.sssforum.utils.Result  {@link }
     * @author lws
    **/
    @PostMapping("/sensitiveWords/add")
    @PreAuthorize("hasAuthority('wxAdmin')")
    public Result addSensitiveWordCheck(@RequestBody SensitiveWordDTO sensitiveWordDTO){
        SensitiveWord sensitiveWord = new SensitiveWord();
        sensitiveWord.setSensitiveWord(sensitiveWordDTO.getSensitiveWord());
        sensitiveWordService.save(sensitiveWord);
        return Result.succes(redisUtil.rPush(CommonConstant.SENSITIVEWORD_LIST,sensitiveWordDTO.getSensitiveWord()));
    }

    /**
     * 敏感词修改
     *
     * @param sensitiveWordDTO
     * @return com.sss.sssforum.utils.Result  {@link }
     * @author lws
    **/
    @PutMapping("/sensitiveWords/change")
    @PreAuthorize("hasAuthority('wxAdmin')")
    public Result updateSensitiveWordCheck(@RequestBody SensitiveWordDTO sensitiveWordDTO){
        redisUtil.lset(CommonConstant.SENSITIVEWORD_LIST,sensitiveWordDTO.getIndex(),sensitiveWordDTO.getChangeValue());
        return Result.succes();
    }
    /**
     * 敏感词删除
     *
     * @param sensitiveWordDTO
     * @return com.sss.sssforum.utils.Result  {@link }
     * @author lws
    **/
    @PutMapping("/sensitiveWords/deleted")
    @PreAuthorize("hasAuthority('wxAdmin')")
    public Result delSensitiveWordCheck(@RequestBody SensitiveWordDTO sensitiveWordDTO){
        Long lrem = redisUtil.lrem(CommonConstant.SENSITIVEWORD_LIST, 0L, sensitiveWordDTO.getSensitiveWord());
        return Result.succes(lrem >0);
    }


    /**
     * 删除帖子评论
     *
     * @param id
     * @return com.sss.sssforum.utils.Result
     * @author lws
     * @date 2020/8/18 17:13
    **/
   @PutMapping("/post-comment/{id}")
   @PreAuthorize("hasAuthority('wxAdmin')")
    public Result delPostComment(@PathVariable Long id, @RequestBody PostCommentDTO postCommentDTO){
       PostComment comment = postCommentService.getById(id);
       if (Objects.isNull(comment)) {
           return Result.fail("评论已被删除");
       }
       forumPostService.update(new LambdaUpdateWrapper<ForumPost>().setSql("comment_count=comment_count - 1").eq(ForumPost::getId,postCommentDTO.getPostId()));
       //往评论表插入数据
       PostComment postComment = new PostComment();
       postComment.setCommentContent(CommonConstant.DELETE_COMMENT_MESSAGE+id).setUserId(Long.valueOf(MessageType.NONEXISTENT.getCode()))
               .setCommentType(MessageType.SYSTEM.getCode()).setPostId(postCommentDTO.getPostId());
       postCommentService.save(postComment);
       //往消息表插入数据
       UserMessage userMessage = new UserMessage();
       userMessage.setUserId(postCommentDTO.getGetMessageUserId()).setPostId(postCommentDTO.getPostId())
               .setMessageType(MessageType.SYSTEM.getCode())
               .setPostCommentId(postComment.getId());
       userMessageService.addMyMessage(userMessage);
       return Result.succes(postCommentService.removeById(id));
   }


   /**
    * 更改用户状态
    *
    * @param wxUser 用户实体
    * @return com.sss.sssforum.utils.Result  {@link }
    * @author lws
   **/
    @PutMapping("/change/user/status")
    @PreAuthorize("hasAuthority('wxAdmin')")
    public Result changeUserStatus(@RequestBody WxUser wxUser){
        wxUserService.update(new LambdaUpdateWrapper<WxUser>().set(WxUser::getState,wxUser.getState()).eq(WxUser::getId,wxUser.getId()));
        return Result.succes();
   }

   /**
    * 设置管理员
    * @param wxUser 用户实体
    * @return com.sss.sssforum.utils.Result  {@link }
    * @author lws
   **/
    @PutMapping("/set/admin")
    @PreAuthorize("hasAuthority('wxAdmin')")
    public Result setIsAdmin(@RequestBody WxUser wxUser){
        wxUserService.update(new LambdaUpdateWrapper<WxUser>().set(WxUser::getRoleId,wxUser.getRoleId()).eq(WxUser::getId,wxUser.getId()));
        return Result.succes();
    }

    /**
     * 设置发布
     * @param wxUser 用户实体
     * @return com.sss.sssforum.utils.Result  {@link }
     * @author lws
     **/
    @PutMapping("/set/del")
    @PreAuthorize("hasAuthority('wxAdmin')")
    public Result setIsPush(@RequestBody WxUser wxUser){
        wxUserService.update(new LambdaUpdateWrapper<WxUser>().set(WxUser::getIsPush,wxUser.getIsPush()).eq(WxUser::getId,wxUser.getId()));
        return Result.succes();
    }

    /**
     * 屏蔽评论内容
     *
     * @param postCommentDTO
     * @return com.sss.sssforum.utils.Result
     * @author lws
     * @date 2020/9/8 9:18
    **/
    @PutMapping("/comment/shielding")
    @PreAuthorize("hasAuthority('wxAdmin')")
    public Result shieldingCommentContent(@RequestBody PostCommentDTO postCommentDTO) {
        return Result.succes(adminService.shieldingCommentContent(postCommentDTO));
    }

    /**
     * 评论内容置顶
     *
     * @param postCommentDTO
     * @return com.sss.sssforum.utils.Result
     * @author lws
     * @date 2020/9/8 9:18
    **/
    @PutMapping("/comment/top")
    @PreAuthorize("hasAuthority('wxAdmin')")
    public Result setCommentContentTop(@RequestBody  PostCommentDTO postCommentDTO) {
        return Result.succes(adminService.setCommentContentTop(postCommentDTO));
    }

}
