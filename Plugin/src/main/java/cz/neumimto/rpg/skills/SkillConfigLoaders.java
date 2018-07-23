package cz.neumimto.rpg.skills;

public class SkillConfigLoaders {


    public static SkillConfigLoader SKILLTREE_PATH = new SkillConfigLoader("specialization") {
        @Override
        public ISkill build(String skillname) {
            return new SkillTreeSpecialization(skillname);
        }
    };
    
    public static SkillConfigLoader ITEM_ACCESS = new SkillConfigLoader("item-access") {
        @Override
        public ISkill build(String skillname) {
            return new ItemAccessSkill(skillname);
        }
    };

    public static SkillConfigLoader ATTRIBUTE = new SkillConfigLoader("attribute") {
        @Override
        public ISkill build(String skillname) {
            return new CharacterAttributeSkill(skillname);
        }
    };

    public static SkillConfigLoader PROPERTY = new SkillConfigLoader("property") {
        @Override
        public ISkill build(String skillname) {
            return new PropertySkill(skillname);
        }
    };
    
}
