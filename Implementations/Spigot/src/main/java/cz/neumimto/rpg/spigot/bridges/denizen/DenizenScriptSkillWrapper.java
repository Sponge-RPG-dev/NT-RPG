package cz.neumimto.rpg.spigot.bridges.denizen;

import cz.neumimto.rpg.common.ResourceLoader;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.SkillData;
import cz.neumimto.rpg.common.skills.SkillResult;
import cz.neumimto.rpg.common.skills.types.ActiveSkill;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;

public class DenizenScriptSkillWrapper extends ActiveSkill<ISpigotCharacter> {

    private String catalogId;

    public DenizenScriptSkillWrapper() {
        ResourceLoader.Skill sk = this.getClass().getAnnotation(ResourceLoader.Skill.class);
        if (sk != null) {
            catalogId = sk.value().toLowerCase();
        }
    }

    @Override
    public SkillResult cast(ISpigotCharacter character, PlayerSkillContext info) {
        EntityCastSkillDenizenEvent event = new EntityCastSkillDenizenEvent();
        event.character = character;
        event.context = info;
        event.fire();
        return SkillResult.OK;
    }

    @Override
    public DenizenSkillData constructSkillData() {
        return new DenizenSkillData(getId());
    }

    public static class DenizenSkillData extends SkillData {

        private String scriptPath;

        public DenizenSkillData(String skill) {
            super(skill);
        }

        public String getScriptPath() {
            return scriptPath;
        }

        public void setScriptPath(String scriptPath) {
            this.scriptPath = scriptPath;
        }
    }
}
