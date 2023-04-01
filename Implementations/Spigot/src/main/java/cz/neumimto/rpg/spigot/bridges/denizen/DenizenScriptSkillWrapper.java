package cz.neumimto.rpg.spigot.bridges.denizen;

import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.SkillData;
import cz.neumimto.rpg.common.skills.SkillResult;
import cz.neumimto.rpg.common.skills.types.ActiveSkill;
import cz.neumimto.rpg.spigot.entities.players.SpigotCharacter;

public class DenizenScriptSkillWrapper extends ActiveSkill<SpigotCharacter> {


    @Override
    public SkillResult cast(SpigotCharacter character, PlayerSkillContext info) {
        EntityCastSkillDenizenEvent event = EntityCastSkillDenizenEvent.instance;
        event.character = character;
        event.context = info;
        event.skillId = info.getSkill().getId();
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
