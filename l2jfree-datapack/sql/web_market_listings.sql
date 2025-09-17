CREATE TABLE IF NOT EXISTS `market_listings` (
  `listing_id`      BIGINT NOT NULL AUTO_INCREMENT,
  `owner_char_id`   INT NOT NULL,
  `owner_account`   VARCHAR(45) NOT NULL DEFAULT '',
  `item_object_id`  INT NOT NULL,      -- object_id из items
  `item_id`         INT NOT NULL,      -- шаблон предмета
  `enchant_level`   INT NOT NULL DEFAULT 0,
  `count`           BIGINT NOT NULL,
  `price_adena`     BIGINT NOT NULL,   -- цена за весь стак
  `currency_item_id` INT NOT NULL DEFAULT 57,
  `status`          ENUM('LISTED','SOLD','CANCELLED','EXPIRED') NOT NULL DEFAULT 'LISTED',
  `created_at`      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `expires_at`      TIMESTAMP NULL DEFAULT NULL,
  PRIMARY KEY (`listing_id`),
  UNIQUE KEY `uk_item_object` (`item_object_id`),
  KEY `idx_owner` (`owner_char_id`,`status`),
  KEY `idx_item` (`item_id`,`status`),
  KEY `idx_status` (`status`)
) DEFAULT CHARSET=utf8;
