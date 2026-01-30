<!-- 答疑表注释 -->

# 答疑表结构
CREATE TABLE `question_ding` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `school_id` bigint(18) unsigned NOT NULL COMMENT '学校id',
  `group_id` bigint(18) unsigned NOT NULL COMMENT '班级id',
  `user_id` bigint(18) unsigned NOT NULL COMMENT '用户id',
  `teacher_id` bigint(18) DEFAULT NULL COMMENT '叮题回复老师',
  `task_id` bigint(18) unsigned NOT NULL COMMENT '作业id',
  `question_id` bigint(18) unsigned NOT NULL COMMENT '题目id',
  `comment` text NOT NULL COMMENT '评论',
  `comment_back_text` text COMMENT '老师对叮题的反馈，文本模式',
  `comment_back_image` varchar(1024) DEFAULT NULL COMMENT '老师对叮题的反馈，图片模式',
  `comment_back_audio` varchar(1024) DEFAULT NULL COMMENT '老师对叮题的反馈，语音模式',
  `comment_back_audio_duration` bigint(18) DEFAULT NULL COMMENT '音频的时长',
  `is_delete` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '删除标记',
  `update_time` datetime NOT NULL COMMENT '最后更新时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `is_share` tinyint(3) DEFAULT '0' COMMENT '0-非全年级共享  1-全年级共享',
  `is_removed_by_manual` tinyint(3) DEFAULT '0' COMMENT '0-默认值 1-人工挪出未答疑（例如线下做了答疑）',
  `pid` bigint(18) DEFAULT '0' COMMENT '父id',
  `is_last` tinyint(3) DEFAULT '1' COMMENT '是否是最后一条，用于列表获取判断是否答疑 0-不是 1-是',
  `comment_back_video` varchar(2048) DEFAULT NULL COMMENT '视频支持',
  `comment_back_video_duration` bigint(18) DEFAULT NULL COMMENT '视频的总时长',
  `is_read` tinyint(3) DEFAULT '0' COMMENT '是否已读, 0-未读 1-已读',
  PRIMARY KEY (`id`),
  KEY `idx_question_task` (`question_id`,`task_id`),
  KEY `idx_teacher_question_task` (`teacher_id`,`task_id`,`question_id`) USING BTREE,
  KEY `idx_user_question_task` (`user_id`,`task_id`,`question_id`) USING BTREE,
  KEY `idx_question_ding_school_id` (`school_id`)
) ENGINE=InnoDB AUTO_INCREMENT=131747 DEFAULT CHARSET=utf8mb4 COMMENT='叮'


# 题目表
CREATE TABLE `question` (
  `id` bigint(18) unsigned NOT NULL AUTO_INCREMENT COMMENT '主题ID',
  `subject_id` int(11) unsigned NOT NULL COMMENT '科目id',
  `trunk_id` bigint(18) unsigned NOT NULL COMMENT '套题关联到题干表，非套题为0',
  `book_id` bigint(11) DEFAULT '0',
  `creator_id` bigint(18) unsigned NOT NULL DEFAULT '0' COMMENT '录入人 0系统录入',
  `content_id` bigint(255) unsigned NOT NULL COMMENT '题目信息id',
  `catalog_id` bigint(11) DEFAULT NULL COMMENT '目录id',
  `page` smallint(3) unsigned NOT NULL COMMENT '页码',
  `origin_type` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '来源类型 0教材1教辅2试卷3老师录题',
  `number` varchar(20) NOT NULL COMMENT '题号',
  `type` tinyint(3) NOT NULL COMMENT '题型',
  `is_objective` tinyint(3) unsigned NOT NULL COMMENT '是否为客观题',
  `origin` varchar(100) DEFAULT NULL COMMENT '录题来源',
  `seq` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '表示顺序，越小越在前,预留两位给小题准备',
  `is_delete` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '删除标记',
  `update_time` datetime NOT NULL COMMENT '最后更新时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `score` int(11) DEFAULT NULL,
  `difficulty` tinyint(3) DEFAULT NULL,
  `textbook_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '对应教材id',
  `textbook_chapter_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '对应教材章id',
  `textbook_section_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '对应教材节id',
  `chapter_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '章id',
  `section_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '节id',
  `keyword` varchar(200) DEFAULT NULL COMMENT '原始的知识点',
  `is_check` int(11) DEFAULT '0' COMMENT '0 - 未检测（默认） 1 - 已检测  99 - 图片无权访问',
  PRIMARY KEY (`id`),
  KEY `idx_catalog` (`catalog_id`),
  KEY `idx_section_id` (`section_id`),
  KEY `idx_book` (`book_id`),
  KEY `idx_trunk` (`trunk_id`),
  KEY `idx_creator` (`creator_id`),
  KEY `idx_content_id` (`content_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=79718404 DEFAULT CHARSET=utf8mb4

# 题目内容表
CREATE TABLE `question_content` (
  `id` bigint(18) unsigned NOT NULL AUTO_INCREMENT COMMENT '主题ID',
  `content` longtext NOT NULL COMMENT '题目',
  `answer` text NOT NULL COMMENT '答案',
  `is_delete` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '删除标记',
  `update_time` datetime NOT NULL COMMENT '最后更新时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=79716750 DEFAULT CHARSET=utf8mb4 COMMENT='题干、答案和解析'

# 题目题干表
CREATE TABLE `question_trunk` (
  `id` bigint(18) unsigned NOT NULL AUTO_INCREMENT COMMENT '主题ID',
  `number` varchar(100) NOT NULL DEFAULT '""' COMMENT '大题题号',
  `content` text NOT NULL COMMENT '题干内容',
  `high` int(11) unsigned NOT NULL DEFAULT '0',
  `is_delete` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '删除标记',
  `update_time` datetime NOT NULL COMMENT '最后更新时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=237337 DEFAULT CHARSET=utf8mb4
