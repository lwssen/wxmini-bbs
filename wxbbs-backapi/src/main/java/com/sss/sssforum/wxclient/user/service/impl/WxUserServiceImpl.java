package com.sss.sssforum.wxclient.user.service.impl;

import com.sss.sssforum.utils.SecurityUtils;
import com.sss.sssforum.wxclient.dto.PostDTO;
import com.sss.sssforum.wxclient.dto.UserDTO;
import com.sss.sssforum.wxclient.post.dao.IForumPostDao;
import com.sss.sssforum.wxclient.post.entity.ForumPost;
import com.sss.sssforum.wxclient.user.entity.WxUser;
import com.sss.sssforum.wxclient.user.dao.IWxUserDao;
import com.sss.sssforum.wxclient.user.service.IWxUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author sss
 * @since 2020-08-06
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WxUserServiceImpl extends ServiceImpl<IWxUserDao, WxUser> implements IWxUserService {
   private final IForumPostDao forumPostDao;
   private final IWxUserDao wxUserDao;


   @Override
    public List<PostDTO> myPostList(String title) {
        ForumPost forumPost = new ForumPost();
        forumPost.setCreateUserId(SecurityUtils.getCurrentUserId()).setIsDeleted(true).setTitle(title);
       return forumPostDao.getPostList(forumPost);
    }

    @Override
    public List<WxUser> getAllUserList(String userName) {
        return wxUserDao.getAllUserList(userName);
    }

    @Transactional(propagation = Propagation.SUPPORTS,readOnly = true)
    public void test(){
        System.out.println("@Transactional失效的第三种场景");
    }

    @Override
    public void testA() {
        System.out.println("@Transactional失效的第五种场景");
        System.out.println("调用了testB方法");
    }

    @Override
    @Transactional
    public void testB() {

    }

    @Override
    @Transactional
    public void testException() {
        try {
            System.out.println("@Transactional失效的第六种场景");
            new RuntimeException("@Transactional失效的第六种场景");
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
