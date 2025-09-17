CREATE TABLE IF NOT EXISTS `market_sales` (
  `sale_id`         BIGINT NOT NULL AUTO_INCREMENT,
  `listing_id`      BIGINT NOT NULL,
  `seller_char_id`  INT NOT NULL,
  `buyer_char_id`   INT NOT NULL,
  `price_adena`     BIGINT NOT NULL,
  `currency_item_id` INT NOT NULL DEFAULT 57,
  `sold_at`         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`sale_id`),
  KEY `idx_listing` (`listing_id`),
  KEY `idx_buyer`   (`buyer_char_id`),
  KEY `idx_seller`  (`seller_char_id`)
) DEFAULT CHARSET=utf8;
