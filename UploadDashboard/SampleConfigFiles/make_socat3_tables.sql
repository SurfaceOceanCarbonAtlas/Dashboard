DROP TABLE IF EXISTS `reviewers`;
CREATE TABLE `reviewers` (
  `reviewer_id` INT(4) UNSIGNED NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(64) NOT NULL DEFAULT '',
  `realname` VARCHAR(64) NOT NULL DEFAULT '',
  `email` VARCHAR(256) NOT NULL DEFAULT '',
  PRIMARY KEY USING BTREE (`reviewer_id`),
  UNIQUE KEY `username` USING BTREE (`username`),
  UNIQUE KEY `realname` USING BTREE (`realname`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `qcflags`;
CREATE TABLE `qcflags` (
  `qc_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `qc_flag` CHAR(1) NOT NULL DEFAULT ' ',
  `expocode` VARCHAR(16) NOT NULL DEFAULT '',
  `socat_version` FLOAT(3,1) NOT NULL DEFAULT '0.0',
  `region_id` CHAR(1) NOT NULL DEFAULT ' ',
  `flag_date` DATETIME NOT NULL DEFAULT '1900-01-01 00:00:00',
  `reviewer_id` INT(4) UNSIGNED NOT NULL DEFAULT '0',
  `qc_comment` VARCHAR(1024) NOT NULL DEFAULT '',
  PRIMARY KEY USING BTREE (`qc_id`),
  KEY `qc_flag` USING BTREE (`qc_flag`),
  KEY `expocode` USING BTREE (`expocode`),
  KEY `socat_version` USING BTREE (`socat_version`),
  KEY `region_id` USING BTREE (`region_id`),
  KEY `flag_date` USING BTREE (`flag_date`),
  KEY `reviewer_id` USING BTREE (`reviewer_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `woceflags`;
CREATE TABLE `woceflags` (
  `woce_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `woce_flag` SMALLINT(1) UNSIGNED NOT NULL DEFAULT '0',
  `expocode` VARCHAR(16) NOT NULL DEFAULT '',
  `socat_version` FLOAT(3,1) NOT NULL DEFAULT '0.0',
  `region_id` CHAR(1) NOT NULL DEFAULT ' ',
  `data_row` INT(6) UNSIGNED DEFAULT NULL,
  `data_longitude` FLOAT(12,6) DEFAULT NULL,
  `data_latitude` FLOAT(12,6) DEFAULT NULL,
  `data_time` BIGINT DEFAULT NULL,
  `data_type` VARCHAR(32) DEFAULT NULL,
  `data_name` VARCHAR(64) DEFAULT NULL,
  `data_value` FLOAT(12,6) DEFAULT NULL,
  `flag_date` DATETIME NOT NULL DEFAULT '1900-01-01 00:00:00',
  `reviewer_id` INT(4) UNSIGNED NOT NULL DEFAULT 0,
  `woce_comment` VARCHAR(1024) NOT NULL DEFAULT '',
  PRIMARY KEY USING BTREE (`woce_id`),
  KEY `woce_flag` USING BTREE (`woce_flag`),
  KEY `expocode` USING BTREE (`expocode`),
  KEY `socat_version` USING BTREE (`socat_version`),
  KEY `region_id` USING BTREE (`region_id`),
  KEY `data_row` USING BTREE (`data_row`),
  KEY `data_longitude` USING BTREE (`data_longitude`),
  KEY `data_latitude` USING BTREE (`data_latitude`),
  KEY `data_time` USING BTREE (`data_time`),
  KEY `data_type` USING BTREE (`data_type`),
  KEY `data_name` USING BTREE (`data_name`),
  KEY `flag_date` USING BTREE (`flag_date`),
  KEY `reviewer_id` USING BTREE (`reviewer_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

