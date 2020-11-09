package com.sss.sssforum.wxclient.post.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sss.sssforum.annotation.SensitiveWordCheck;
import com.sss.sssforum.base.BaseController;
import com.sss.sssforum.config.redisconfig.RedisUtil;
import com.sss.sssforum.constant.CommonConstant;
import com.sss.sssforum.enums.MessageType;
import com.sss.sssforum.utils.MyFileUtils;
import com.sss.sssforum.utils.Result;
import com.sss.sssforum.utils.SecurityUtils;
import com.sss.sssforum.wxclient.dto.MyUserDetails;
import com.sss.sssforum.wxclient.dto.PostDTO;
import com.sss.sssforum.wxclient.dto.UserDTO;
import com.sss.sssforum.wxclient.dto.UserMessageDTO;
import com.sss.sssforum.wxclient.message.entity.UserMessage;
import com.sss.sssforum.wxclient.message.service.IUserMessageService;
import com.sss.sssforum.wxclient.post.entity.ForumPost;
import com.sss.sssforum.wxclient.post.entity.PostComment;
import com.sss.sssforum.wxclient.post.service.IForumPostService;
import com.sss.sssforum.wxclient.post.service.IPostCommentService;
import com.sss.sssforum.wxclient.user.entity.WxUser;
import com.sss.sssforum.wxclient.user.service.IWxUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 *
 * 帖子有关的所有接口
 *
 *
 * @author sss
 * @since 2020-07-28
 */
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class ForumPostController extends BaseController {

    private final IForumPostService forumPostService;
    private final RedisUtil redisUtil;
    private final IWxUserService wxUserService;
    private final IUserMessageService userMessageService;
    private final IPostCommentService postCommentService;
    private final ResourceLoader resourceLoader;


    /**
     * 帖子列表
     *
     * @param typeId 类型ID
     * @return com.sss.sssforum.utils.Result<com.github.pagehelper.PageInfo<com.sss.sssforum.wxclient.post.entity.ForumPost>>
     * @author lws
     * @date 2020/8/7 15:03
    **/
    @GetMapping
    public Result<PageInfo<PostDTO>> postList(@RequestParam(required = false)  Integer typeId,
                                              @RequestParam(required = false)  String title,
                                              @RequestParam(required = false)  String orderValue){

        if (StringUtils.isNotBlank(orderValue)) {
            startPage("fp."+orderValue+" desc,fp.create_time","desc");
        }else {
            startPage("fp.create_time","desc");
        }
        if (typeId !=null&& typeId==9){
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            String header = requestAttributes.getRequest().getHeader("Author-Token");
            if (StringUtils.isBlank(header)){
                return Result.succes(PageInfo.of(Collections.emptyList()));
            }
            WxUser currentUser = SecurityUtils.getCurrentUser();
            if (currentUser ==null || !currentUser.getRoleId().equals(CommonConstant.ADMIN)){
                return Result.succes(PageInfo.of(Collections.emptyList()));
            }
        }
        List<PostDTO> list = forumPostService.getPostList(typeId,true,title);
        try {
            WxUser currentUser = SecurityUtils.getCurrentUser();
            if (currentUser ==null || !currentUser.getRoleId().equals(CommonConstant.ADMIN)){
                List<PostDTO> filterList = list.stream().filter(k -> k.getTypesId() != 9).collect(Collectors.toList());
                PageInfo<PostDTO> pageInfo = new PageInfo<>(filterList);
                return Result.succes(pageInfo);
            }
        }catch (Exception e){
            List<PostDTO> filterList = list.stream().filter(k -> k.getTypesId() != 9).collect(Collectors.toList());
            PageInfo<PostDTO> pageInfo = new PageInfo<>(filterList);
            return Result.succes(pageInfo);
        }
        PageInfo<PostDTO> pageInfo = new PageInfo<>(list);
        return Result.succes(pageInfo);
    }

    /**
     * 帖子详情
     *
     * @param id 帖子ID
     * @param anonymousUserId 匿名用户ID
     * @return com.sss.sssforum.utils.Result
     * @author lws
     * @date 2020/8/7 15:03
    **/
    @GetMapping("/info/{id}")
    public Result getPostInfo(@PathVariable Long id, @RequestParam(required = false) String anonymousUserId, HttpServletRequest request){
        Long currentUserId = SecurityUtils.getCurrentUserId();
        String authorToken = request.getHeader("Author-Token");
        if (StringUtils.isBlank(authorToken) && StringUtils.isNotBlank(anonymousUserId)){
            if (!redisUtil.exists(CommonConstant.USER_VIEW_PREFIX+anonymousUserId+"-"+id)) {
                redisUtil.set(CommonConstant.USER_VIEW_PREFIX+anonymousUserId+"-"+id,"view", CommonConstant.USER_VIEW_EXPIRED,TimeUnit.HOURS);
                forumPostService.increViewCount(id);
            }
        }else {
            if (!redisUtil.exists(CommonConstant.USER_VIEW_PREFIX+currentUserId+"-"+id)) {
                redisUtil.set(CommonConstant.USER_VIEW_PREFIX+currentUserId+"-"+id,"view", CommonConstant.USER_VIEW_EXPIRED,TimeUnit.HOURS);
                forumPostService.increViewCount(id);
            }
        }

        ForumPost forumPost = forumPostService.getById(id);
        if(Objects.isNull(forumPost)){
            return Result.fail();
        }
        WxUser wxUser = wxUserService.getOne(new LambdaQueryWrapper<WxUser>().eq(WxUser::getId, forumPost.getCreateUserId()));
        forumPost.setWxUser(wxUser);
        Integer userId = Integer.valueOf(SecurityUtils.getCurrentUserId() + "");
        if(!CommonConstant.ANONYMOUSUSER_INTEGER.equals(userId)){
            Boolean isLike = redisUtil.getBit(CommonConstant.LIKE_POST + id, Integer.valueOf(SecurityUtils.getCurrentUserId() + ""));
            forumPost.setIsLike(isLike);
        }
        return Result.succes(forumPost);
    }

    /**
     * 发布帖子
     *
     * @param forumPost
     * @return com.sss.sssforum.utils.Result
     * @author lws
     * @date 2020/8/7 15:03
    **/
    @PostMapping("/push")
    @SensitiveWordCheck
    public Result pushPost(@RequestBody @Valid ForumPost forumPost){
        forumPost.setCreateUserId(SecurityUtils.getCurrentUserId()).setTypesId(1);
        return Result.succes(forumPostService.save(forumPost));
    }


    /**
     * 点赞帖子
     *
     * @param userMessageDTO
     * @return com.sss.sssforum.utils.Result
     * @author lws
     * @date 2020/8/13 10:06
    **/
    @PutMapping("/like")
    public Result likePost(@RequestBody UserMessageDTO userMessageDTO){
        return Result.succes(forumPostService.likePost(userMessageDTO));
   }


   /**
    * 文件上传
    *
    * @param file
    * @return com.sss.sssforum.utils.Result
    * @author lws
    * @date 2020/8/17 15:50
   **/
    @PostMapping("/image/upload")
    public Result imageUpload(MultipartFile file) {
        return Result.succes(forumPostService.ossUploadFile(file));
    }

    @GetMapping("/download")
    public void templateDownload(HttpServletResponse response, HttpServletRequest request) throws Exception {

        InputStream inputStream = null;
        ServletOutputStream servletOutputStream = null;
        String filename="题目导入模板.xlsx";
//		response.setCharacterEncoding("utf-8");
//		response.setContentType("multipart/form-data");
//		response.setHeader("Content-Disposition",
//				"attachment;fileName=" + FileUtils.setFileDownloadHeader(request, "题目导入模板.xlsx"));
        response.setContentType("application/vnd.ms-excel");
        response.addHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.addHeader("charset", "utf-8");
        response.addHeader("Pragma", "no-cache");
        String encodeName = URLEncoder.encode(filename, StandardCharsets.UTF_8.toString());
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + encodeName + "\"; filename*=utf-8''" + encodeName);
        Resource resource = resourceLoader.getResource("classpath:static/excel/题目导入模板.xlsx");
        inputStream = resource.getInputStream();
        servletOutputStream = response.getOutputStream();
        IOUtils.copy(inputStream,servletOutputStream);
        response.flushBuffer();
        if (servletOutputStream != null) {
            servletOutputStream.close();
        }
            inputStream.close();

    }
}

