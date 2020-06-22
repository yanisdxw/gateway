CREATE TABLE `account` (
    `num` varchar(255) DEFAULT NULL,
    `name` varchar(255) DEFAULT NULL,
    `blance` double(10,2) DEFAULT '0.00',
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `createTime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;