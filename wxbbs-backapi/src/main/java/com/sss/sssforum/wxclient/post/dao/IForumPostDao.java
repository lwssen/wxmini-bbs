package com.sss.sssforum.wxclient.post.dao;

import com.sss.sssforum.wxclient.dto.PostDTO;
import com.sss.sssforum.wxclient.dto.PostTypeDTO;
import com.sss.sssforum.wxclient.post.entity.ForumPost;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 * 所有帖子数据表 Mapper 接口
 * </p>
 *
 * @author sss
 * @since 2020-07-28
 */
public interface IForumPostDao extends BaseMapper<ForumPost> {


    /**
     *  帖子回复数增加
     *
     * @param postId 帖子ID
     * @return int
     * @date 2020/8/4 8:41
    **/
    @Update("update forum_post set comment_count=comment_count+1 where id=#{postId}")
    int increCommentCount(@Param("postId") Long postId);

    @Update("update forum_post set view_count=view_count+1 where id=#{postId}")
    int increViewCount(@Param("postId") Long postId);

    @Update("update forum_post set like_count=like_count+1 where id=#{postId}")
    int increLikeCount(@Param("postId") Long postId);

    @Update("update forum_post set like_count=like_count-1 where id=#{postId}")
    int decreLikeCount(@Param("postId") Long postId);

    List<PostDTO> getPostList(@Param("forumPost") ForumPost forumPost);

    Integer updatePostStatus(@Param("postId")Long  postId,@Param("isDeleted") Integer isDeleted);

    Integer updatePost(@Param("postTypeDTO") PostTypeDTO postTypeDTO);
}
