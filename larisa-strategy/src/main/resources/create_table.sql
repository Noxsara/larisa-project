CREATE TABLE `kline` (
  `id` BIGINT(11) NOT NULL,
  `symbol` VARCHAR(10) NOT NULL,
  `open` DECIMAL(9,4) NOT NULL,
  `close` DECIMAL(9,4) NOT NULL,
  `high` DECIMAL(9,4) NOT NULL,
  `low` DECIMAL(9,4) NOT NULL,
  `count` BIGINT(11) NOT NULL,
  `vol` DOUBLE NOT NULL,
  `amount` BIGINT(11) NOT NULL,
  UNIQUE KEY idx_symbol_id(`symbol`, `id`)
) ENGINE=INNODB  DEFAULT CHARSET=UTF8MB4;

CREATE TABLE `order` (
  `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
  `client_order_id` BIGINT(11) NOT NULL,
  `order_id` BIGINT(11) NOT NULL,
  `kline_id` BIGINT(11) NOT NULL,
  `symbol` VARCHAR(10) NOT NULL,
  `vol` DOUBLE NOT NULL,
  `price` DECIMAL(9,4) NOT NULL,
  `lever_rate` TINYINT NOT NULL,
  `position_rate` DECIMAL(9,4) NOT NULL,
  `risk_rate` DECIMAL(9,4) NOT NULL,
  `created` TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY idx_symbol_client_order_id(symbol, client_order_id),
  INDEX idx_symbol(symbol)
)ENGINE=INNODB  DEFAULT CHARSET=UTF8MB4;