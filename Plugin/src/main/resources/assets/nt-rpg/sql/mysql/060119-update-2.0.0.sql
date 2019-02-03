--@author:NeumimTo
--@date:06.01.2018 17:00
--@note:Delete race column
--@id:delete-race-column
alter table rpg_character_class drop column race;

--@author:NeumimTo
--@date:03.02.2019 14:00
--@note:Add Level Column
--@id:add-level-column
alter table rpg_character_class add column level INT after experiences;