DROP DATABASE IF EXISTS `sss-forum-prod`;
CREATE DATABASE `sss-forum-prod` CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci';
USE `sss-forum-prod`;
-- ----------------------------
-- Table structure for file_info
-- ----------------------------
DROP TABLE IF EXISTS `file_info`;
CREATE TABLE `file_info`  (
  `id` int(32) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件名称',
  `save_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件存放位置',
  `request_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '外界访问路径',
  `size` bigint(100) NOT NULL COMMENT '文件大小 单位(K)',
  `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '文件信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for forum_post
-- ----------------------------
DROP TABLE IF EXISTS `forum_post`;
CREATE TABLE `forum_post`  (
  `id` bigint(32) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '帖子ID',
  `types_id` int(32) NULL DEFAULT NULL COMMENT '类别id',
  `digest` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '帖子简介',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标题',
  `post_cotent` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '帖子内容',
  `create_user_id` bigint(32) NOT NULL COMMENT '发布人ID',
  `comment_count` int(255) NULL DEFAULT 0 COMMENT '回复条数',
  `view_count` int(255) NULL DEFAULT 0 COMMENT '查看次数',
  `like_count` int(255) NULL DEFAULT 0 COMMENT '点赞次数',
  `order_number` int(100) NOT NULL DEFAULT 0 COMMENT '排序',
  `is_deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除 0 正常 1 删除',
  `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '所有帖子数据表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for post_comment
-- ----------------------------
DROP TABLE IF EXISTS `post_comment`;
CREATE TABLE `post_comment`  (
  `id` bigint(32) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `post_id` bigint(32) NOT NULL COMMENT '帖子ID 对应forum_post表的ID',
  `parent_id` int(32) NOT NULL DEFAULT 0 COMMENT '父级ID',
  `comment_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '评论内容',
  `comment_type` int(10) NOT NULL DEFAULT 1 COMMENT '1 他人评论或回复  3 点赞',
  `user_id` bigint(32) NOT NULL COMMENT '评论人ID',
  `comment_like_count` int(255) NOT NULL DEFAULT 0 COMMENT '评论点赞数',
  `old_comment_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '被替换的评论内容',
  `order_number` int(100) NOT NULL DEFAULT 0 COMMENT '排序',
  `is_deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除 0 正常  1 删除',
  `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '帖子评论表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for post_type
-- ----------------------------
DROP TABLE IF EXISTS `post_type`;
CREATE TABLE `post_type`  (
  `type_id` bigint(32) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '类型ID',
  `type_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '类型名称',
  `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`type_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '帖子类型表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of post_type
-- ----------------------------
INSERT INTO `post_type` VALUES (1, '全部', '2020-09-11 17:31:23', '2020-09-11 17:31:23');
INSERT INTO `post_type` VALUES (2, '问题', '2020-09-11 17:31:40', '2020-09-11 17:31:40');
INSERT INTO `post_type` VALUES (3, '动漫', '2020-09-11 17:31:45', '2020-09-11 17:31:45');
INSERT INTO `post_type` VALUES (4, 'JAVA', '2020-09-11 17:31:50', '2020-09-11 17:31:50');

-- ----------------------------
-- Table structure for sensitive_word
-- ----------------------------
DROP TABLE IF EXISTS `sensitive_word`;
CREATE TABLE `sensitive_word`  (
  `id` int(32) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `sensitive_word` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '敏感词',
  `is_deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除 0 正常  1删除',
  `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '敏感词表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_message
-- ----------------------------
DROP TABLE IF EXISTS `user_message`;
CREATE TABLE `user_message`  (
  `id` bigint(32) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `post_comment_id` bigint(32) NOT NULL COMMENT '评论内容ID 对应post_comment表的ID',
  `user_id` bigint(32) NOT NULL COMMENT '用户ID',
  `like_comment_user_id` bigint(32) NOT NULL DEFAULT 0 COMMENT '点赞或回复评论或评论帖子的用户ID',
  `post_id` bigint(32) NOT NULL COMMENT '帖子ID',
  `message_type` int(12) NOT NULL DEFAULT 1 COMMENT '消息类型 1 帖子评论 2 楼层回复 3 点赞 4 系统通知',
  `has_read` int(10) NOT NULL DEFAULT 0 COMMENT '是否已读 0 未读 1 已读',
  `is_deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除 0 正常 1删除',
  `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for wx_role
-- ----------------------------
DROP TABLE IF EXISTS `wx_role`;
CREATE TABLE `wx_role`  (
  `id` int(32) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色名称',
  `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of wx_role
-- ----------------------------
INSERT INTO `wx_role` VALUES (1, 'wxAdmin', '2020-09-11 17:30:28', '2020-09-11 17:30:28');
INSERT INTO `wx_role` VALUES (2, 'wxUser', '2020-09-11 17:30:37', '2020-09-11 17:30:37');

-- ----------------------------
-- Table structure for wx_user
-- ----------------------------
DROP TABLE IF EXISTS `wx_user`;
CREATE TABLE `wx_user`  (
  `id` bigint(32) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `wx_nickname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名称',
  `open_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '微信用户唯一标识',
  `role_id` int(10) NOT NULL DEFAULT 2 COMMENT '角色ID',
  `avatar_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '头像地址',
  `subscribe` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否关注公众号 0 关注 1 未关注',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '用户地址',
  `state` int(10) NOT NULL DEFAULT 0 COMMENT '用户状态 0 正常',
  `is_push` tinyint(1) NOT NULL COMMENT '是否允许发布 0 禁止 1 允许',
  `is_deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除 0 正常 1 删除',
  `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
