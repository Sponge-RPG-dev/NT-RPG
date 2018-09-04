package cz.neumimto.rpg.skills.configs;

import cz.neumimto.rpg.skills.ItemAccessSkill;
import cz.neumimto.rpg.skills.parents.CharacterAttributeSkill;
import cz.neumimto.rpg.skills.parents.PropertySkill;
import cz.neumimto.rpg.skills.tree.SkillTreeSpecialization;

public class SkillConfigLoaders {

	public static SkillConfigLoader SKILLTREE_PATH = new SkillConfigLoader("specialization", SkillTreeSpecialization.class);

	public static SkillConfigLoader ITEM_ACCESS = new SkillConfigLoader("item-access", ItemAccessSkill.class);

	public static SkillConfigLoader ATTRIBUTE = new SkillConfigLoader("attribute", CharacterAttributeSkill.class);

	public static SkillConfigLoader PROPERTY = new SkillConfigLoader("property", PropertySkill.class);

}
