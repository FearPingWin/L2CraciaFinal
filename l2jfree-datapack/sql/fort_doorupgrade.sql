-- ----------------------------
-- Table structure for fort_doorupgrade
-- ----------------------------
CREATE TABLE IF NOT EXISTS `fort_doorupgrade` (
  `doorId` int(11) NOT NULL DEFAULT '0',
  `fortId` int(11) NOT NULL,
  `hp` int(11) NOT NULL DEFAULT '0',
  `pDef` int(11) NOT NULL DEFAULT '0',
  `mDef` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY  (`doorId`)
) DEFAULT CHARSET=utf8;

