package cz.neumimto.rpg.skills;

public class SkillConfigLoaders {

    public static SkillConfigLoader SKILLTREE_PATH = new SkillConfigLoader("specialization", SkillTreeSpecialization.class);
    
    public static SkillConfigLoader ITEM_ACCESS = new SkillConfigLoader("item-access", ItemAccessSkill.class);

    public static SkillConfigLoader ATTRIBUTE = new SkillConfigLoader("attribute", CharacterAttributeSkill.class);

    public static SkillConfigLoader PROPERTY = new SkillConfigLoader("property", PropertySkill.class);

    public static SkillConfigLoader EFFECT_PASSIVE_SKILL = new SkillConfigLoader("effect", ConfigPassiveSkill.class);
}
