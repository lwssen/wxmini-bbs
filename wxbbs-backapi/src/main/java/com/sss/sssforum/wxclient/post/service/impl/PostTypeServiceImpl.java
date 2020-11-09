package com.sss.sssforum.wxclient.post.service.impl;

import com.sss.sssforum.wxclient.post.entity.PostType;
import com.sss.sssforum.wxclient.post.dao.IPostTypeDao;
import com.sss.sssforum.wxclient.post.service.IPostTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 帖子类型表 服务实现类
 * </p>
 *
 * @author sss
 * @since 2020-07-30
 */
@Service
public class PostTypeServiceImpl extends ServiceImpl<IPostTypeDao, PostType> implements IPostTypeService {

}
