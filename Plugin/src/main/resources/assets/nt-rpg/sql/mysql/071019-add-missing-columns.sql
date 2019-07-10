--@id:add-missing-columns
--@author:NeumimTo
--@date:10.07.2019 23:00
--@note:Add some missing column
alter table rpg_character_attribute add column created timestamp not null default now();
alter table rpg_character_attribute add column updated timestamp not null default now();
alter table rpg_character_class add column updated timestamp not null default now();
alter table rpg_character_class add column created timestamp not null default now();
alter table rpg_character_skill add column created timestamp not null default now();
alter table rpg_character_skill add column updated timestamp not null default now();