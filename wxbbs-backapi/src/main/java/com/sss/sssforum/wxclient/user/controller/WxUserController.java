package com.sss.sssforum.wxclient.user.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageInfo;
import com.sss.sssforum.annotation.SensitiveWordCheck;
import com.sss.sssforum.base.BaseController;
import com.sss.sssforum.constant.CommonConstant;
import com.sss.sssforum.utils.Result;
import com.sss.sssforum.utils.SecurityUtils;
import com.sss.sssforum.wxclient.dto.PostDTO;
import com.sss.sssforum.wxclient.post.entity.ForumPost;
import com.sss.sssforum.wxclient.post.service.IForumPostService;
import com.sss.sssforum.wxclient.role.entity.WxRole;
import com.sss.sssforum.wxclient.role.service.IWxRoleService;
import com.sss.sssforum.wxclient.user.entity.WxUser;
import com.sss.sssforum.wxclient.user.service.IWxUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

/**
 * 用户相关接口列表
 *
 * @author sss
 * @since 2020-08-06
 */
@RestController
@RequestMapping("/my")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WxUserController extends BaseController {

    private final IWxUserService wxUserService;
    private final IForumPostService forumPostService;
    private final IWxRoleService wxRoleService;

    /**
     * 获得我的信详细信息
     *
     * @return com.sss.sssforum.utils.Result<com.sss.sssforum.wxclient.user.entity.WxUser>  {@link }
     * @author lws
    **/
    @GetMapping
    public Result<WxUser> getUserInfo(){
        Long userId = SecurityUtils.getCurrentUserId();
        WxUser wxUser = wxUserService.getById(userId);
        ForumPost forumPost = forumPostService.getOne(new QueryWrapper<ForumPost>()
                .select("count(create_user_id) as commentCount,sum(like_count) as likeCount")
                .eq("create_user_id", userId));
        wxUser.setPostTotalCount(forumPost.getCommentCount()).setLikeTotalCount(forumPost.getLikeCount());
        return Result.succes(wxUser);
    }

    /**
     * 修改我的信息
     *
     * @param wxUser
     * @return com.sss.sssforum.utils.Result<com.sss.sssforum.wxclient.user.entity.WxUser>  {@link }
     * @author lws
    **/
    @PutMapping
    @SensitiveWordCheck
    public Result<WxUser> updateUserInfo(@RequestBody WxUser wxUser){
        Long userId = SecurityUtils.getCurrentUserId();
        wxUser.setId(userId);
       wxUserService.updateById(wxUser);
       wxUser.setOpenId(null);
        return Result.succes(wxUser);
    }

    /**
     * 查询我的帖子列表
     *
     * @return com.sss.sssforum.utils.Result<com.github.pagehelper.PageInfo<com.sss.sssforum.wxclient.dto.PostDTO>>  {@link }
     * @author lws
    **/
    @GetMapping("/posts")
    public Result<PageInfo<PostDTO>> myPostList(String title){
        startPage("fp.create_time","desc");
        List<PostDTO> list = wxUserService.myPostList(title);
        PageInfo<PostDTO> pageInfo = new PageInfo<>(list);
        return Result.succes(pageInfo);
    }

    /**
     * 删除我的帖子
     *
     * @param id 帖子ID
     * @return com.sss.sssforum.utils.Result  {@link }
     * @author lws
     **/
    @DeleteMapping("/posts/{id}")
    public Result delPost(@PathVariable Long id){
        return Result.succes(forumPostService.removeById(id));
    }

    /**
     * 查询当前用户是否是管理员
     *
     * @return com.sss.sssforum.utils.Result  {@link }
     * @author lws
    **/
    @GetMapping("/check/admin")
    public Result getUserRoleInfo(){
        WxUser wxUser = wxUserService.getById(SecurityUtils.getCurrentUserId());
        WxRole wxRole = wxRoleService.getById(wxUser.getRoleId());
        boolean isAdmin = CommonConstant.ADMIN.equals(wxRole.getId()) && CommonConstant.ADMIN_NAME.equals(wxRole.getRoleName());
        HashMap<String, Boolean> map = new HashMap<>(1);
        map.put("isAdmin",isAdmin);
        return Result.succes(map);
    }


    @GetMapping("/test")
    public Result test(){
        WxUser currentUser = SecurityUtils.getCurrentUser();
        return Result.succes();
    }
}

