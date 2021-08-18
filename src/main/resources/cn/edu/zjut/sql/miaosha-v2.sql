/*
 Navicat Premium Data Transfer

 Source Server         : ECS-MySQL5
 Source Server Type    : MySQL
 Source Server Version : 50568
 Source Host           : 47.113.188.176:3306
 Source Schema         : miaosha

 Target Server Type    : MySQL
 Target Server Version : 50568
 File Encoding         : 65001

 Date: 18/08/2021 16:02:38
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for item_info
-- ----------------------------
DROP TABLE IF EXISTS `item_info`;
CREATE TABLE `item_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(64) NOT NULL DEFAULT '',
  `price` double(10,0) NOT NULL DEFAULT '0',
  `description` varchar(500) NOT NULL DEFAULT '',
  `sales` int(11) NOT NULL DEFAULT '0',
  `img_url` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of item_info
-- ----------------------------
BEGIN;
INSERT INTO `item_info` VALUES (2, 'iphone13', 8000, '秒杀进行中', 0, 'https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fww3.sinaimg.cn%2Fmw690%2F002HCNrbgy1gtex9esk8hj618g0p0dix02.jpg&refer=http%3A%2F%2Fwww.sina.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1631434931&t=f4de0f0d6ed039094150971b69678eaf');
INSERT INTO `item_info` VALUES (3, '红薯', 5, '秒杀已结束 from ECS', 0, 'https://img1.baidu.com/it/u=3103435753,3987114076&fm=26&fmt=auto&gp=0.jpg');
INSERT INTO `item_info` VALUES (4, '显卡', 4000, '未有秒杀活动', 0, 'https://gimg2.baidu.com/image_search/src=http%3A%2F%2F2a.zol-img.com.cn%2Fproduct%2F10_400x300%2F470%2FcetpeL8gHcjU.jpg&refer=http%3A%2F%2F2a.zol-img.com.cn&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1631865230&t=4d92d3662277e02e158df5b807367e3');
COMMIT;

-- ----------------------------
-- Table structure for item_stock
-- ----------------------------
DROP TABLE IF EXISTS `item_stock`;
CREATE TABLE `item_stock` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `stock` int(11) NOT NULL DEFAULT '0',
  `item_id` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `item_id_index` (`item_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of item_stock
-- ----------------------------
BEGIN;
INSERT INTO `item_stock` VALUES (1, 800, 2);
INSERT INTO `item_stock` VALUES (2, 1000, 3);
INSERT INTO `item_stock` VALUES (3, 500, 4);
COMMIT;

-- ----------------------------
-- Table structure for item_stock_log
-- ----------------------------
DROP TABLE IF EXISTS `item_stock_log`;
CREATE TABLE `item_stock_log` (
  `id` varchar(64) NOT NULL,
  `item_id` int(11) NOT NULL DEFAULT '0',
  `amount` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of item_stock_log
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for order_info
-- ----------------------------
DROP TABLE IF EXISTS `order_info`;
CREATE TABLE `order_info` (
  `id` varchar(32) NOT NULL,
  `user_id` int(11) NOT NULL DEFAULT '0',
  `item_id` int(11) NOT NULL DEFAULT '0',
  `amount` int(11) NOT NULL DEFAULT '0',
  `item_price` double NOT NULL DEFAULT '0',
  `order_price` double NOT NULL DEFAULT '0',
  `promo_id` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of order_info
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for promo
-- ----------------------------
DROP TABLE IF EXISTS `promo`;
CREATE TABLE `promo` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `promo_name` varchar(255) NOT NULL DEFAULT '',
  `start_date` datetime NOT NULL DEFAULT '1000-01-01 00:00:00',
  `end_date` datetime NOT NULL DEFAULT '1000-01-01 00:00:00',
  `item_id` int(11) NOT NULL DEFAULT '0',
  `promo_item_price` double NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of promo
-- ----------------------------
BEGIN;
INSERT INTO `promo` VALUES (2, '红薯抢购', '2021-08-13 17:00:00', '2021-08-13 20:00:00', 3, 2);
INSERT INTO `promo` VALUES (3, 'iphone秒杀', '2021-08-17 20:28:00', '2022-10-10 20:25:19', 2, 5000);
COMMIT;

-- ----------------------------
-- Table structure for sequence_info
-- ----------------------------
DROP TABLE IF EXISTS `sequence_info`;
CREATE TABLE `sequence_info` (
  `name` varchar(20) NOT NULL,
  `current_value` int(11) NOT NULL,
  `step` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sequence_info
-- ----------------------------
BEGIN;
INSERT INTO `sequence_info` VALUES ('order_info', 36, 1);
COMMIT;

-- ----------------------------
-- Table structure for user_info
-- ----------------------------
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL DEFAULT '',
  `gender` tinyint(4) NOT NULL DEFAULT '0' COMMENT '1代表男性，0代表女性',
  `age` int(11) NOT NULL DEFAULT '0',
  `telephone` varchar(255) NOT NULL DEFAULT '',
  `register_mode` varchar(255) NOT NULL DEFAULT '' COMMENT 'by phone, by WeChat, by Alipay',
  `third_party_id` varchar(64) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  UNIQUE KEY `telephone_unique_index` (`telephone`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user_info
-- ----------------------------
BEGIN;
INSERT INTO `user_info` VALUES (12, 'choushao', 1, 22, '19967309302', 'by phone', '');
INSERT INTO `user_info` VALUES (13, 'pkh', 1, 150, '18257555020', 'by phone', '');
COMMIT;

-- ----------------------------
-- Table structure for user_password
-- ----------------------------
DROP TABLE IF EXISTS `user_password`;
CREATE TABLE `user_password` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `encrypt_password` varchar(128) NOT NULL DEFAULT '',
  `user_id` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user_password
-- ----------------------------
BEGIN;
INSERT INTO `user_password` VALUES (8, 'ICy5YqxZB1uWSwcVLSNLcA==', 12);
INSERT INTO `user_password` VALUES (9, 'ICy5YqxZB1uWSwcVLSNLcA==', 13);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
