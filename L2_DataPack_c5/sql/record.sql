CREATE TABLE IF NOT EXISTS `record` ( 
  `maxplayer` int(5) NOT NULL default '0', 
  `date` DATE NOT NULL 
) ENGINE=MyISAM;

INSERT INTO `record` VALUES (0, NOW());