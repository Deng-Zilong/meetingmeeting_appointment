-- MySQL dump 10.13  Distrib 5.7.20, for Win64 (x86_64)
--
-- Host: localhost    Database: test
-- ------------------------------------------------------
-- Server version	5.7.20-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `attendees`
--

DROP TABLE IF EXISTS `attendees`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `attendees` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '会议id',
  `gmt_create` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) COMMENT '添加时间',
  `gmt_modified` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  `user_id` int(11) NOT NULL COMMENT '参与人id',
  `meeting_record_id` bigint(20) NOT NULL COMMENT '会议预约id',
  `is_delete` tinyint(1) DEFAULT NULL COMMENT '0未删除 1删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `attendees`
--

LOCK TABLES `attendees` WRITE;
/*!40000 ALTER TABLE `attendees` DISABLE KEYS */;
/*!40000 ALTER TABLE `attendees` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `meeting_group`
--

DROP TABLE IF EXISTS `meeting_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `meeting_group` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '群组id',
  `gmt_create` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) COMMENT '添加时间',
  `gmt_modified` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  `adminid` int(11) NOT NULL COMMENT '创建人id',
  `name` varchar(50) NOT NULL COMMENT '群组名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `meeting_group`
--

LOCK TABLES `meeting_group` WRITE;
/*!40000 ALTER TABLE `meeting_group` DISABLE KEYS */;
/*!40000 ALTER TABLE `meeting_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `meeting_record`
--

DROP TABLE IF EXISTS `meeting_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `meeting_record` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '预约id',
  `gmt_create` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) COMMENT '添加时间',
  `gmt_modified` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  `title` varchar(50) NOT NULL COMMENT '会议主题',
  `start_time` datetime(3) NOT NULL COMMENT '开始时间',
  `end_time` datetime(3) NOT NULL COMMENT '结束时间',
  `date_time` datetime(3) NOT NULL COMMENT '日期',
  `place` varchar(50) NOT NULL COMMENT '地点',
  `description` varchar(255) DEFAULT NULL COMMENT '会议概述',
  `meeting_room_id` int(11) NOT NULL COMMENT '会议室名称ID',
  `status` tinyint(1) DEFAULT NULL COMMENT '会议状态1空闲2使用中3暂停使用',
  `adminid` int(11) NOT NULL COMMENT '创建人id',
  `is_delete` tinyint(1) DEFAULT NULL COMMENT '0未删除 1删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `meeting_record`
--

LOCK TABLES `meeting_record` WRITE;
/*!40000 ALTER TABLE `meeting_record` DISABLE KEYS */;
/*!40000 ALTER TABLE `meeting_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `meeting_room`
--

DROP TABLE IF EXISTS `meeting_room`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `meeting_room` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `gmt_create` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) COMMENT '添加时间',
  `gmt_modified` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  `room_name` varchar(50) NOT NULL COMMENT '会议室名称',
  `location` varchar(50) NOT NULL COMMENT '会议室位置',
  `number` int(11) NOT NULL COMMENT '容量',
  `tool` varchar(50) DEFAULT NULL COMMENT '设备',
  `status` tinyint(1) NOT NULL COMMENT '会议状态1空闲2使用中3暂停使用',
  `is_delete` tinyint(1) NOT NULL COMMENT '0未删除 1删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `meeting_room`
--

LOCK TABLES `meeting_room` WRITE;
/*!40000 ALTER TABLE `meeting_room` DISABLE KEYS */;
/*!40000 ALTER TABLE `meeting_room` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_group`
--

DROP TABLE IF EXISTS `user_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_group` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '群组id',
  `gmt_create` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) COMMENT '添加时间',
  `gmt_modified` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  `user_id` int(11) NOT NULL COMMENT '用户id',
  `user_name` varchar(50) NOT NULL COMMENT '群组名称',
  `is_delete` tinyint(1) DEFAULT NULL COMMENT '0未删除 1删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_group`
--

LOCK TABLES `user_group` WRITE;
/*!40000 ALTER TABLE `user_group` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'test'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-04-24 10:57:41
