package com.sss.sssforum.wxclient.post.dao;

import com.sss.sssforum.wxclient.dto.PostCommentDTO;
import com.sss.sssforum.wxclient.post.entity.PostComment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import javafx.geometry.Pos;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 * 帖子评论表 Mapper 接口
 * </p>
 *
 * @author sss
 * @since 2020-08-03
 */
public interface IPostCommentDao extends BaseMapper<PostComment> {


    /**
     * 根据帖子ID查询出评论列表
     *
     * @param postId  帖子ID
     * @return java.util.List<com.sss.sssforum.wxclient.dto.PostCommentDTO>  {@link }
     * @author lws
    **/
    List<PostCommentDTO> getPostCommentList(@Param("postId")Long postId,@Param("commentContent")String commentContent
            ,@Param("parentId") Integer parentId,@Param("isDeleted") Boolean isDeleted );

    /**
     * 评论的点赞数加 1
     *
     * @param postCommentId 评论ID
     * @return
     */
    @Update("update post_comment set comment_like_count=comment_like_count +1 where id=#{postCommentId} ")
    Integer incrLikePostComment(@Param("postCommentId") Long postCommentId);

    /**
     * 评论的点赞数减 1
     *
     * @param postCommentId 评论ID
     * @return
     */
    @Update("update post_comment set comment_like_count=comment_like_count -1 where id=#{postCommentId} ")
    Integer decrLikePostComment(@Param("postCommentId") Long postCommentId);

    /**
     *
     * 更新评论类型 为3 的是否删除状态改变
     *
     * @param postComment
     * @return
     */
    Integer updatePostCommentState(@Param("postComment") PostComment postComment);
    PostComment myGetPostComment(PostComment postComment);

   //@Select("SELECT id,post_id,parent_id,comment_content,user_id,comment_type,comment_like_count,is_deleted,create_time,update_time FROM post_comment WHERE id=#{id}")
    PostComment selectIsDeletedById(@Param("id") Long id);

}
