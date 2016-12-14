DROP TABLE IF EXISTS `Reviewers`;

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

