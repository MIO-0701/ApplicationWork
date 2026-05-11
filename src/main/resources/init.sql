-- ### 盲人用户 mang_ren_db
-- - id  作为主键，int
-- - name 用户姓名，String
-- - zhang_hao 用户账号，String
-- - password 密码，String
-- - sex 性别，int，1为男0为女
-- - su_du 习惯配速，int
-- - gong_li 常跑公里数，int
-- - is_del 是否被删除标志，int，0为未删除1为删除
-- - create_time 创建时间，date
-- - updata_time 修改时间，date
--
-- ### 志愿者 zhi_yuan_db
-- - id  作为主键，int
-- - name 用户姓名，String
-- - zhang_hao 用户账号，String
-- - password 密码，String
-- - sex 性别，int，1为男0为女
-- - su_du 习惯配速，int
-- - gong_li 常跑公里数，int
-- - zai_xian 常在线时间段，String，格式为“0000”
-- - ping_fen 评分，int，上限为5分
-- - is_ren_zhen 是否有认证，int
-- - is_del 是否被删除标志，int，0为未删除1为删除
-- - create_time 创建时间，date
-- - updata_time 修改时间，date
-- ### 预约记录 yu_yue_db
-- - id  作为主键，int
-- - mang_ren_id 盲人id，int
-- - zhi_yuan_id 志愿者id，int
-- - di_dian 预约位置，string
-- - is_del 是否删除标志，int
-- - create_time 预约时间，date
-- ### 跑步记录 run_db
-- - id 作为主键,int
-- - user_type 用户类型，int，1代表志愿者0代表盲人
-- - user_id 用户id，int
-- - su_du 平均速度，int，但为2位小数\*100
-- - shi_chang 跑步用时，int
-- - ju_li 跑步距离，int，但为2位小数\*100

create database android_work_db;
use android_work_db;

CREATE TABLE `mang_ren` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `zhang_hao` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `sex` int DEFAULT NULL,
  `su_du` int DEFAULT NULL,
  `gong_li` int DEFAULT NULL,
  `is_del` int DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `updata_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
);
CREATE TABLE `zhi_yuan` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `zhang_hao` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `sex` int DEFAULT NULL,
  `su_du` int DEFAULT NULL,
  `gong_li` int DEFAULT NULL,
  `zai_xian` varchar(255) DEFAULT NULL,
  `ping_fen` int DEFAULT NULL,
  `is_ren_zhen` int DEFAULT NULL,
  `is_del` int DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `updata_time` datetime DEFAULT NULL,
    PRIMARY KEY (`id`)
);
CREATE TABLE `yu_yue` (
  `id` int NOT NULL AUTO_INCREMENT,
  `mang_ren_id` int DEFAULT NULL,
  `zhi_yuan_id` int DEFAULT NULL,
  `di_dian` varchar(255) DEFAULT NULL,
  `is_del` int DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
);
CREATE TABLE `run` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_type` int DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  `su_du` int DEFAULT NULL,
  `shi_chang` int DEFAULT NULL,
  `ju_li` int DEFAULT NULL,
  `is_del` int DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
);