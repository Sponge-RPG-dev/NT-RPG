--@id:fix-null-level
--@author:NeumimTo
--@date:25.06.2019 18:20
--@note:Add column for attribute points spent sum
update table rpg_character_base add column attribute_points_spent int default 0;