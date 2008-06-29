-- ---------------------------
-- Table structure for `character_skills_save`
-- ---------------------------
CREATE TABLE IF NOT EXISTS `character_skills_save` (
  `charId` INT UNSIGNED NOT NULL DEFAULT 0,
  `skill_id` INT NOT NULL DEFAULT 0,
  `skill_level` SMALLINT UNSIGNED NOT NULL DEFAULT 0,
  `effect_count` INT NOT NULL DEFAULT 0,
  `effect_cur_time` INT NOT NULL DEFAULT 0,
  `reuse_delay` INT(8) NOT NULL DEFAULT 0,
  `restore_type` INT(1) NOT NULL DEFAULT 0,
  `class_index` INT(1) NOT NULL DEFAULT 0,
  `buff_index` INT(2) NOT NULL DEFAULT 0,
  PRIMARY KEY (`charId`,`skill_id`,`class_index`)
) DEFAULT CHARSET=utf8;