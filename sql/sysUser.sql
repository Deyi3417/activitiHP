CREATE TABLE `sys_user` (
                            `user_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
                            `user_account` VARCHAR(30) DEFAULT NULL COMMENT '登录账号',
                            `user_name` VARCHAR(30) DEFAULT NULL COMMENT '用户名称',
                            `user_type` VARCHAR(2) DEFAULT '0' COMMENT '用户类型0-系统，1-外部',
                            `email` VARCHAR(50) DEFAULT '' COMMENT '用户邮箱',
                            `phone` VARCHAR(11) DEFAULT '' COMMENT '手机号码',
                            `sex` CHAR(1) DEFAULT '0' COMMENT '性别0-男 1-女',
                            `password` VARCHAR(50) DEFAULT '' COMMENT '密码',
                            `status` CHAR(1) DEFAULT '0' COMMENT '账号状态0-正常 1-停用',
                            `del_flag` CHAR(1) DEFAULT '0' COMMENT '删除标志0-未删除 1-已删除',
                            `create_name` VARCHAR(64) DEFAULT '' COMMENT '创建者',
                            `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
                            `update_name` VARCHAR(64) DEFAULT '' COMMENT '更新者',
                            `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
                            PRIMARY KEY (`user_id`)
) ENGINE=INNODB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表'