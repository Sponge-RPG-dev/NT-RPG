package cz.neumimto.rpg.spigot.bridges.denizen;

import com.denizenscript.denizen.events.BukkitScriptEvent;
import com.denizenscript.denizen.utilities.implementation.BukkitScriptEntryData;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.scripts.ScriptEntryData;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.spigot.bridges.denizen.tags.CharacterTag;
import cz.neumimto.rpg.spigot.bridges.denizen.tags.SkillContextTag;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;

public class EntityCastSkillDenizenEvent extends BukkitScriptEvent {

    public ISpigotCharacter character;
    public PlayerSkillContext context;
    public String skillId;

    public static EntityCastSkillDenizenEvent instance;

    public EntityCastSkillDenizenEvent() {
        this.registerCouldMatcher("<entity> casts skill");
        this.registerSwitches("id");
        instance = this;
    }


    @Override
    public boolean matches(ScriptPath path) {
        String entity = path.eventArgLowerAt(0);
        String casts = path.eventArgLowerAt(1);
        String skills = path.eventArgLowerAt(2);
        String skillId = path.switches.get("id");
        if (this.skillId.equalsIgnoreCase(skillId)) {
            return true;
        }
        return super.matches(path);
    }

    @Override
    public String getName() {
        return "EntityCastsSkill";
    }

    public ObjectTag getContext(String name) {
        return switch (name) {
            case "caster" -> new CharacterTag(character);
            case "skill_context" -> new SkillContextTag(context, character);
            case "skill_id" -> new ElementTag(skillId);
            default -> super.getContext(name);
        };
    }

    public ScriptEntryData getScriptEntryData() {
        return new BukkitScriptEntryData(character.getPlayer());
    }


}
