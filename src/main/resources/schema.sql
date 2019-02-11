-- MySQL dump 10.13  Distrib 5.5.38, for osx10.6 (i386)
--
-- Host: localhost    Database: snapshot
-- ------------------------------------------------------
-- Server version	5.5.38

SET FOREIGN_KEY_CHECKS=0;
CREATE TABLE IF NOT EXISTS `snapshot` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `modified` datetime NOT NULL,
  `description` longtext,
  `end_date` datetime DEFAULT NULL,
  `name` longtext NOT NULL,
  `snapshot_date` datetime NOT NULL,
  `host` varchar(255) NOT  NULL,
  `port` int(11) NOT NULL,
  `space_id` varchar(255) NOT NULL,
  `store_id` varchar(255) NOT NULL,
  `start_date` datetime DEFAULT NULL,
  `status` varchar(255) NOT NULL,
  `status_text` longtext,
  `user_email` varchar(255) NOT NULL,
  `member_id` varchar(128) DEFAULT NULL,
  `total_size_in_bytes` bigint(20) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Table structure for table `restoration`
--
CREATE TABLE IF NOT EXISTS `restoration` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `modified` datetime NOT NULL,
  `host` varchar(255) NOT NULL,
  `port` int(11) NOT NULL,
  `space_id` varchar(255) NOT NULL,
  `store_id` varchar(255) NOT NULL,
  `end_date` datetime DEFAULT NULL,
  `start_date` datetime DEFAULT NULL,
  `expiration_date` datetime DEFAULT NULL,
  `status` varchar(255) NOT NULL,
  `status_text` longtext,
  `user_email` varchar(255) NOT NULL,
  `snapshot_id` bigint(20) NOT NULL,
  `restoration_id` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_RESTORATION_ID` (`restoration_id`),
  KEY `FK_ejb7a5btov5hyhb0pyvo3yeb7` (`snapshot_id`),
  CONSTRAINT `FK_ejb7a5btov5hyhb0pyvo3yeb7` FOREIGN KEY (`snapshot_id`) REFERENCES `snapshot` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Table structure for table `snapshot_content_item`
--
CREATE TABLE IF NOT EXISTS `snapshot_content_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `modified` datetime NOT NULL,
  `content_id` longtext COLLATE utf8_bin NOT NULL,
  `content_id_hash` varchar(50) COLLATE utf8_bin NOT NULL,
  `metadata` longtext COLLATE utf8_bin,
  `snapshot_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_30tots9ry24rjg42xn08egdrl` (`snapshot_id`,`content_id_hash`),
  CONSTRAINT `FK_bif6fhum5u975ks9uo9xufbjh` FOREIGN KEY (`snapshot_id`) REFERENCES `snapshot` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Table structure for table `snapshot_alternate_ids`
--
CREATE TABLE IF NOT EXISTS `snapshot_alternate_ids` (
  `snapshot_id` bigint(20) NOT NULL,
  `snapshot_alternate_id` varchar(255) DEFAULT NULL,
  KEY `FK_q9se3kgdc5eebpwxqq4843bu3` (`snapshot_id`),
  CONSTRAINT `FK_q9se3kgdc5eebpwxqq4843bu3` FOREIGN KEY (`snapshot_id`) REFERENCES `snapshot` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Table structure for table `snapshot_history`
--
CREATE TABLE IF NOT EXISTS `snapshot_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `modified` datetime DEFAULT NULL,
  `history` longtext DEFAULT NULL,
  `history_date` datetime DEFAULT NULL,
  `snapshot_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FK_ff91lsj23rrrs3nuovf3hofwl` FOREIGN KEY (`snapshot_id`) REFERENCES `snapshot` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

SET FOREIGN_KEY_CHECKS=1;
