SET TIME_ZONE='+00:00';

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `USER_ID` BIGINT NOT NULL AUTO_INCREMENT,
  `USERNAME` VARCHAR(45) NOT NULL,
  `PASSWORD` VARCHAR(100) NOT NULL,
  `ENABLED` BIT(1) NOT NULL,
  `EMAIL` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`USER_ID`)
) DEFAULT CHARSET=latin1;
INSERT INTO `users` VALUES('1','guest','84983c60f7daadc1cb8698621f802c0d9f9a3c3c295c810748fb048115c186ec',true,'guest@nowhere.org');

--
-- Table structure for table `user_roles`
--

DROP TABLE IF EXISTS `user_roles`;
CREATE TABLE `user_roles` (
  `USER_ROLE_ID` BIGINT NOT NULL AUTO_INCREMENT,
  `USER_ID` BIGINT NOT NULL,
  `AUTHORITY` VARCHAR(45) CHARACTER SET utf8 NOT NULL,
  PRIMARY KEY (`USER_ROLE_ID`),
  KEY `FK_user_roles` (`USER_ID`),
  CONSTRAINT `FK_user_roles` FOREIGN KEY (`USER_ID`) REFERENCES `users` (`USER_ID`)
) DEFAULT CHARSET=latin1;
INSERT INTO `user_roles` VALUES('1','1','USER_ROLE');

--
-- Table structure for table `author`
--

DROP TABLE IF EXISTS `author`;
CREATE TABLE `author` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `organization` VARCHAR(100) DEFAULT NULL,
  `address` VARCHAR(255) DEFAULT NULL,
  `phone` VARCHAR(25) DEFAULT NULL,
  `email` VARCHAR(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `key_info` (`name`,`phone`,`email`)
) DEFAULT CHARSET=utf8;

--
-- Table structure for table `files`
--

DROP TABLE IF EXISTS `files`;
CREATE TABLE `files` (
  `id` VARCHAR(50) NOT NULL,
  `file_location` TEXT NOT NULL,
  `creator_email` VARCHAR(100) NOT NULL,
  `file_status` VARCHAR(20) NOT NULL,
  `update_date` VARCHAR(20) NOT NULL,
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=latin1;

--
-- Table structure for table `survey`
--

DROP TABLE IF EXISTS `survey`;
CREATE TABLE `survey` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `survey_name` varchar(300) DEFAULT NULL,
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=latin1;

--
-- Table structure for table `variables`
--

DROP TABLE IF EXISTS `variables`;
CREATE TABLE `variables` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `description` VARCHAR(300) DEFAULT NULL,
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8;

--
-- Table structure for table `vessel`
--

DROP TABLE IF EXISTS `vessel`;
CREATE TABLE `vessel` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `vesselName` VARCHAR(250) NOT NULL,
  `vesselId` VARCHAR(150) DEFAULT NULL,
  `country` VARCHAR(150) DEFAULT NULL,
  `vesselOwner` VARCHAR(250) DEFAULT NULL,
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=latin1;

