package com.sss.sssforum.constant;

/**
 * 通用常量
 *
 * @author lws
 * @date 2020-08-07 15:19
 **/
public class CommonConstant {

    public static final String USER_VIEW_PREFIX="user:view:";

    public static final Long USER_VIEW_EXPIRED=1L;

    /**
     * 当前记录起始索引
     */
    public static final String PAGE_NUM = "pageNum";

    /**
     * 每页显示记录数
     */
    public static final String PAGE_SIZE = "pageSize";

    /**
     * 排序列
     */
    public static final String ORDER_BY_COLUMN = "orderByColumn";

    /**
     * 排序的方向 "desc" 或者 "asc".
     */
    public static final String IS_ASC = "isAsc";
    public static final String WX_TOKEN = "wx:token";
    public static final String EXPIRE_TIME = "expireTime ";

    /**
     * 点赞的帖子
     */
    public static  final String LIKE_POST="like:post:";

    /**
     * 点赞他人评论或回复的内容
     */
    public static  final String LIKE_POST_COMMENT="like:post:comment:";

    public static  final String SYSTEM_MESSAGE="系统通知";
    public static  final String SYSTEM_MESSAGE_TEMPLATE="您的帖子因包含敏感信息，已被管理员删除";

    public static  final String MOVE_POST_MESSAGE="转移帖子通知,类型ID@";
    public static  final String MOVE_POST_MESSAGE_TEMPLATE="您的帖子已被转移到";

    public static  final String DELETE_COMMENT_MESSAGE="删除评论通知,评论ID@";
    public static  final String DELETE_COMMENT_MESSAGE_TEMPLATE="您的评论因包含敏感信息，已被管理员删除";
    public static  final String SHIELDING_COMMENT_MESSAGE="屏蔽评论通知,评论ID@";
    public static  final String SHIELDING_COMMENT_MESSAGE_TEMPLATE="您的评论因包含敏感信息，已被管理员屏蔽";
    public static  final String SHIELDING_COMMENT_CONTENT="该用户发言已被屏蔽";


    /**
     * 管理员ID
     */
    public static  final Integer ADMIN=1;
    /**
     * 匿名用户
     */
    public static  final Integer ANONYMOUSUSER_INTEGER=-999;
    public static  final Long ANONYMOUSUSER_lONG=-999L;

    /**
     * 管理员名称
     */
    public static  final String ADMIN_NAME="wxAdmin";


    /**
     * 铭感词前缀
     */
    public static  final String SENSITIVEWORD_LIST="sensitiveWord:list";
    /**
     * 敏感词替换字符串
     */
    public static  final String SENSITIVEWORD_REPLACE="***";


}
