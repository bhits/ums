CREATE TABLE `user_avatar` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `file_contents` longblob NOT NULL,
  `file_name` varchar(255) NOT NULL,
  `file_extension` varchar(255) NOT NULL,
  `file_size_bytes` bigint(20) NOT NULL,
  `file_width_pixels` bigint(20) NOT NULL,
  `file_height_pixels` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UNQ_user_id_idx` (`user_id`),
  KEY `FK_user_idx` (`user_id`),
  CONSTRAINT `FK_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
);
