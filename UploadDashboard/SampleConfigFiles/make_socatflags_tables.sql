DROP TABLE IF EXISTS `WOCELocations`;
DROP TABLE IF EXISTS `WOCEEvents`;
DROP TABLE IF EXISTS `QCEvents`;
DROP TABLE IF EXISTS `Reviewers`;
DROP TABLE IF EXISTS `Regions`;

CREATE TABLE `Regions` (
  `region_id` CHAR(1) NOT NULL DEFAULT ' ',
  `region_name` VARCHAR(32) NOT NULL DEFAULT '',
  PRIMARY KEY (`region_id`)
) DEFAULT CHARSET=latin1;
INSERT INTO `Regions` (`region_id`, `region_name`) VALUES
  ('A', 'North Atlantic'),
  ('C', 'Coastal'),
  ('G', 'Global'),
  ('I', 'Indian'),
  ('N', 'North Pacific'), 
  ('O', 'Southern Ocean'),
  ('R', 'Arctic'),
  ('T', 'Tropical Pacific'),
  ('Z', 'Tropical Atlantic');

CREATE TABLE `Reviewers` (
  `reviewer_id` INT(4) UNSIGNED NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(64) NOT NULL DEFAULT '',
  `realname` VARCHAR(64) NOT NULL DEFAULT '',
  `email` VARCHAR(256) NOT NULL DEFAULT '',
  PRIMARY KEY (`reviewer_id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `realname` (`realname`)
) DEFAULT CHARSET=latin1;
INSERT INTO `Reviewers` (`realname`, `username`) VALUES
  ('automated data checker', 'automated.data.checker');

CREATE TABLE `QCEvents` (
  `qc_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `qc_flag` CHAR(1) NOT NULL DEFAULT ' ',
  `qc_time` BIGINT DEFAULT NULL,
  `expocode` VARCHAR(16) NOT NULL DEFAULT '',
  `socat_version` CHAR(4) NOT NULL DEFAULT '',
  `region_id` CHAR(1) NOT NULL DEFAULT ' ',
  `reviewer_id` INT(4) UNSIGNED NOT NULL DEFAULT '0',
  `qc_comment` VARCHAR(1024) NOT NULL DEFAULT '',
  PRIMARY KEY (`qc_id`),
  KEY `qc_flag` (`qc_flag`),
  KEY `qc_time` (`qc_time`),
  KEY `expocode` (`expocode`),
  KEY `socat_version` (`socat_version`),
  KEY `region_id` (`region_id`),
  KEY `reviewer_id` (`reviewer_id`),
  CONSTRAINT `QCEvents_region_id` FOREIGN KEY (`region_id`) REFERENCES `Regions` (`region_id`),
  CONSTRAINT `QCEvents_reviewer_id` FOREIGN KEY (`reviewer_id`) REFERENCES `Reviewers` (`reviewer_id`)
) DEFAULT CHARSET=latin1;

CREATE TABLE `WOCEEvents` (
  `woce_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `woce_flag` CHAR(1) NOT NULL DEFAULT ' ',
  `woce_time` BIGINT DEFAULT NULL,
  `expocode` VARCHAR(16) NOT NULL DEFAULT '',
  `socat_version` CHAR(4) NOT NULL DEFAULT '',
  `data_name` VARCHAR(64) NOT NULL DEFAULT '',
  `reviewer_id` INT(4) UNSIGNED NOT NULL DEFAULT 0,
  `woce_comment` VARCHAR(1024) NOT NULL DEFAULT '',
  PRIMARY KEY (`woce_id`),
  KEY `woce_flag` (`woce_flag`),
  KEY `woce_time` (`woce_time`),
  KEY `expocode` (`expocode`),
  KEY `socat_version` (`socat_version`),
  KEY `data_name` (`data_name`),
  KEY `reviewer_id` (`reviewer_id`),
  CONSTRAINT `WOCEEvents_reviewer_id` FOREIGN KEY (`reviewer_id`) REFERENCES `Reviewers` (`reviewer_id`)
) DEFAULT CHARSET=latin1;

CREATE TABLE `WOCELocations` (
  `wloc_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `woce_id` BIGINT UNSIGNED NOT NULL DEFAULT '0',
  `region_id` CHAR(1) NOT NULL DEFAULT ' ',
  `row_num` INT(6) UNSIGNED DEFAULT NULL,
  `longitude` FLOAT(12,6) DEFAULT NULL,
  `latitude` FLOAT(12,6) DEFAULT NULL,
  `data_time` BIGINT DEFAULT NULL,
  `data_value` FLOAT(12,6) DEFAULT NULL,
  PRIMARY KEY (`wloc_id`),
  KEY `woce_id` (`woce_id`),
  KEY `region_id` (`region_id`),
  KEY `row_num` (`row_num`),
  KEY `longitude` (`longitude`),
  KEY `latitude` (`latitude`),
  KEY `data_time` (`data_time`),
  CONSTRAINT `WOCEPoints_region_id` FOREIGN KEY (`region_id`) REFERENCES `Regions` (`region_id`),
  CONSTRAINT `WOCEPoints_woce_id` FOREIGN KEY (`woce_id`) REFERENCES `WOCEEvents` (`woce_id`)
) DEFAULT CHARSET=latin1;

