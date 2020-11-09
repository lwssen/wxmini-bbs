package com.sss.sssforum.wxclient.post.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sss.sssforum.utils.Result;
import com.sss.sssforum.wxclient.post.entity.PostType;
import com.sss.sssforum.wxclient.post.service.IPostTypeService;
import javafx.geometry.Pos;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *
 * 帖子类型接口
 *
 *
 * @author sss
 * @since 2020-07-30
 */
@RestController
@RequestMapping("/post-types")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PostTypeController {
    private final IPostTypeService postTypeService;

    @GetMapping
    public Result<List<PostType>> getPostTypes(){
        return  Result.succes(postTypeService.list());
    }
    @GetMapping("/options")
    public Result<List<PostType>> getOptions(){
        return  Result.succes(postTypeService.list(new QueryWrapper<PostType>().select("type_id as value,type_name as text")));
    }
}

