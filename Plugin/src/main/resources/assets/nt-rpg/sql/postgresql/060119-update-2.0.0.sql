--@id:delete-race-column
--@author:NeumimTo
--@date:06.01.2018 17:00
--@note:Delete race column
alter table rpg_character_base drop column race;
alter table rpg_character_class add column level INT;
alter table rpg_character_base drop column character_cooldowns;
alter table rpg_character_skill add column cooldown BIGINT;