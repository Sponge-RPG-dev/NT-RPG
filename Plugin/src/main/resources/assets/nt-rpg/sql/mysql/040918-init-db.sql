--@author:NeumimTo
--@date:04.09.2018
--@note:Initial setup
--@id:init-db
CREATE TABLE `rpg_character_attribute` (
  `attribute_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `level` int(11) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `character_id` bigint(20) NOT NULL,
  PRIMARY KEY (`attribute_id`),
  KEY `fk_rpg_character_attribute_charid` (`character_id`)
);

CREATE TABLE `rpg_character_base` (
  `character_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created` datetime DEFAULT NULL,
  `updated` datetime DEFAULT NULL,
  `attribute_points` int(11) DEFAULT NULL,
  `can_reset_skills` bit(1) DEFAULT NULL,
  `character_cooldowns` text,
  `guild_id` bigint(20) DEFAULT NULL,
  `health_scale` double DEFAULT NULL,
  `info` varchar(255) DEFAULT NULL,
  `inventory_equip_slot_order` text,
  `last_known_player_name` varchar(16) DEFAULT NULL,
  `last_reset_time` datetime DEFAULT NULL,
  `marked_for_removal` bit(1) DEFAULT NULL,
  `messageType` varchar(255) DEFAULT NULL,
  `name` varchar(40) DEFAULT NULL,
  `primary_class` varchar(255) DEFAULT NULL,
  `race` varchar(255) DEFAULT NULL,
  `used_attribute_points` int(11) DEFAULT NULL,
  `uuid` varchar(255) DEFAULT NULL,
  `version` bigint(20) DEFAULT NULL,
  `world` varchar(255) DEFAULT NULL,
  `X` int(11) DEFAULT NULL,
  `Y` int(11) DEFAULT NULL,
  `Z` int(11) DEFAULT NULL,
  PRIMARY KEY (`character_id`),
  KEY `idx_rpg_character_base_uuid` (`uuid`)
);

CREATE TABLE `rpg_character_class` (
  `class_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `experiences` double DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `skillpoints` int(11) DEFAULT NULL,
  `used_skil_points` int(11) DEFAULT NULL,
  `character_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`classId`),
  KEY `fk_rpg_character_class_charId` (`character_id`)
);

CREATE TABLE `rpg_character_skill` (
  `skill_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `catalog_id` varchar(255) DEFAULT NULL,
  `level` int(11) NOT NULL,
  `character_id` bigint(20) DEFAULT NULL,
  `class_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`skill_id`),
  KEY `fk_rpg_character_skill_charid` (`character_id`),
  KEY `fk_rpg_character_skill_classid` (`class_id`)
);
