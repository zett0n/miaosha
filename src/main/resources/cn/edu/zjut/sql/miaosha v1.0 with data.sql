/*
 Navicat Premium Data Transfer

 Source Server         : MySQL-8.0.23
 Source Server Type    : MySQL
 Source Server Version : 80023
 Source Host           : localhost:3306
 Source Schema         : miaosha

 Target Server Type    : MySQL
 Target Server Version : 80023
 File Encoding         : 65001

 Date: 12/08/2021 00:27:50
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for item_info
-- ----------------------------
DROP TABLE IF EXISTS `item_info`;
CREATE TABLE `item_info` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(64) NOT NULL DEFAULT '',
  `price` double(10,0) NOT NULL DEFAULT '0',
  `description` varchar(500) NOT NULL DEFAULT '',
  `sales` int NOT NULL DEFAULT '0',
  `img_url` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of item_info
-- ----------------------------
BEGIN;
INSERT INTO `item_info` VALUES (2, 'aada', 213, '123', 0, '13123123');
INSERT INTO `item_info` VALUES (3, '红薯', 4, '好吃听得见', 9, 'https://img1.baidu.com/it/u=3103435753,3987114076&fm=26&fmt=auto&gp=0.jpg');
COMMIT;

-- ----------------------------
-- Table structure for item_stock
-- ----------------------------
DROP TABLE IF EXISTS `item_stock`;
CREATE TABLE `item_stock` (
  `id` int NOT NULL AUTO_INCREMENT,
  `stock` int NOT NULL DEFAULT '0',
  `item_id` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `fk_stock_item` (`item_id`),
  CONSTRAINT `fk_stock_item` FOREIGN KEY (`item_id`) REFERENCES `item_info` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of item_stock
-- ----------------------------
BEGIN;
INSERT INTO `item_stock` VALUES (1, 1312, 2);
INSERT INTO `item_stock` VALUES (2, 1190, 3);
COMMIT;

-- ----------------------------
-- Table structure for order_info
-- ----------------------------
DROP TABLE IF EXISTS `order_info`;
CREATE TABLE `order_info` (
  `id` varchar(32) NOT NULL,
  `user_id` int NOT NULL DEFAULT '0',
  `item_id` int NOT NULL DEFAULT '0',
  `amount` int NOT NULL DEFAULT '0',
  `item_price` double NOT NULL DEFAULT '0',
  `order_price` double NOT NULL DEFAULT '0',
  `promo_id` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `fk_user_info_id` (`user_id`),
  KEY `fk_item_info_id` (`item_id`),
  CONSTRAINT `fk_item_info_id` FOREIGN KEY (`item_id`) REFERENCES `item_info` (`id`),
  CONSTRAINT `fk_user_info_id` FOREIGN KEY (`user_id`) REFERENCES `user_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of order_info
-- ----------------------------
BEGIN;
INSERT INTO `order_info` VALUES ('2021080900000000', 12, 3, 1, 0, 0, 0);
INSERT INTO `order_info` VALUES ('2021080900000100', 16, 3, 1, 4, 4, 0);
INSERT INTO `order_info` VALUES ('2021081000000200', 13, 3, 1, 2, 2, 2);
INSERT INTO `order_info` VALUES ('2021081000000300', 13, 3, 1, 2, 2, 2);
INSERT INTO `order_info` VALUES ('2021081000000400', 13, 3, 1, 2, 2, 2);
INSERT INTO `order_info` VALUES ('2021081000000500', 13, 3, 1, 2, 2, 2);
INSERT INTO `order_info` VALUES ('2021081000000600', 13, 3, 1, 2, 2, 2);
INSERT INTO `order_info` VALUES ('2021081000000700', 13, 3, 1, 4, 4, 0);
INSERT INTO `order_info` VALUES ('2021081100000800', 16, 3, 1, 4, 4, 0);
INSERT INTO `order_info` VALUES ('2021081100000900', 16, 3, 1, 4, 4, 0);
COMMIT;

-- ----------------------------
-- Table structure for promo
-- ----------------------------
DROP TABLE IF EXISTS `promo`;
CREATE TABLE `promo` (
  `id` int NOT NULL AUTO_INCREMENT,
  `promo_name` varchar(20) NOT NULL DEFAULT '',
  `start_date` datetime NOT NULL DEFAULT '9999-12-31 00:00:00',
  `end_date` datetime NOT NULL DEFAULT '9999-12-31 00:00:00',
  `item_id` int NOT NULL DEFAULT '0',
  `promo_item_price` double NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `promo_item_info_id_fk` (`item_id`),
  CONSTRAINT `promo_item_info_id_fk` FOREIGN KEY (`item_id`) REFERENCES `item_info` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of promo
-- ----------------------------
BEGIN;
INSERT INTO `promo` VALUES (2, '红薯抢购', '2021-08-10 16:58:00', '2021-08-10 17:00:00', 3, 2);
COMMIT;

-- ----------------------------
-- Table structure for sequence_info
-- ----------------------------
DROP TABLE IF EXISTS `sequence_info`;
CREATE TABLE `sequence_info` (
  `name` varchar(20) NOT NULL,
  `current_value` int NOT NULL,
  `step` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sequence_info
-- ----------------------------
BEGIN;
INSERT INTO `sequence_info` VALUES ('order_info', 10, 1);
COMMIT;

-- ----------------------------
-- Table structure for user_info
-- ----------------------------
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL DEFAULT '',
  `gender` tinyint NOT NULL DEFAULT '0' COMMENT '1代表男性，0代表女性',
  `age` int NOT NULL DEFAULT '0',
  `telephone` varchar(20) NOT NULL DEFAULT '',
  `register_mode` varchar(64) NOT NULL DEFAULT '' COMMENT 'by phone, by WeChat, by Alipay',
  `third_party_id` varchar(64) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  UNIQUE KEY `telephone_unique_index` (`telephone`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user_info
-- ----------------------------
BEGIN;
INSERT INTO `user_info` VALUES (1, 'choushao', 1, 22, '19967309555', 'by phone', '');
INSERT INTO `user_info` VALUES (12, 'Faze Zett0n', 1, 22, '19967309302', 'by phone', '');
INSERT INTO `user_info` VALUES (13, '21ac', 1, 150, '18257555020', 'by phone', '');
INSERT INTO `user_info` VALUES (15, 'pgz', 1, 21, '19967309205', 'by phone', '');
INSERT INTO `user_info` VALUES (16, 'ssl', 1, 21, '18257555021', 'by phone', '');
INSERT INTO `user_info` VALUES (18, '阿擦擦', 1, 122, '19967309309', 'by phone', '');
COMMIT;

-- ----------------------------
-- Table structure for user_password
-- ----------------------------
DROP TABLE IF EXISTS `user_password`;
CREATE TABLE `user_password` (
  `id` int NOT NULL AUTO_INCREMENT,
  `encrypt_password` varchar(128) NOT NULL DEFAULT '',
  `user_id` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `user_password_user_info_id_fk` (`user_id`),
  CONSTRAINT `user_password_user_info_id_fk` FOREIGN KEY (`user_id`) REFERENCES `user_info` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user_password
-- ----------------------------
BEGIN;
INSERT INTO `user_password` VALUES (1, '123', 1);
INSERT INTO `user_password` VALUES (8, 'ICy5YqxZB1uWSwcVLSNLcA==', 12);
INSERT INTO `user_password` VALUES (9, 'ICy5YqxZB1uWSwcVLSNLcA==', 13);
INSERT INTO `user_password` VALUES (10, 'LP1FYFOfiHpeQgQSs3CzYQ==', 15);
INSERT INTO `user_password` VALUES (11, 'ICy5YqxZB1uWSwcVLSNLcA==', 16);
INSERT INTO `user_password` VALUES (12, 'ICy5YqxZB1uWSwcVLSNLcA==', 18);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
