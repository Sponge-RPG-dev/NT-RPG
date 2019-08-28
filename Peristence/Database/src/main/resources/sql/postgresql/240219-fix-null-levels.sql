--@id:fix-null-level
--@author:NeumimTo
--@date:24.02.2019 16:00
--@note:Level was null
update table rpg_character_class set level = 0 where level is null;