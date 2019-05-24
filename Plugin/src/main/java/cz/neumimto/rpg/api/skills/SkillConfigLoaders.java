package cz.neumimto.rpg.api.skills;

import cz.neumimto.rpg.api.skills.tree.SkillTreeSpecialization;
import cz.neumimto.rpg.api.skills.types.CharacterAttributeSkill;
import cz.neumimto.rpg.sponge.skills.ItemAccessSkill;

public class SkillConfigLoaders {

    public static SkillConfigLoader SKILLTREE_PATH = new SkillConfigLoader("specialization", SkillTreeSpecialization.class);

    public static SkillConfigLoader ITEM_ACCESS = new SkillConfigLoader("item-access", ItemAccessSkill.class);

    public static SkillConfigLoader ATTRIBUTE = new SkillConfigLoader("attribute", CharacterAttributeSkill.class);

    public static SkillConfigLoader PROPERTY = new SkillConfigLoader("property", PropertySkill.class);

}
