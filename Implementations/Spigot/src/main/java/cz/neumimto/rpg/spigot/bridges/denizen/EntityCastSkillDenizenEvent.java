package cz.neumimto.rpg.spigot.bridges.denizen;

import com.denizenscript.denizen.events.BukkitScriptEvent;
import com.denizenscript.denizen.utilities.implementation.BukkitScriptEntryData;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.scripts.ScriptEntryData;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.spigot.bridges.denizen.tags.CharacterTag;
import cz.neumimto.rpg.spigot.bridges.denizen.tags.SkillContextTag;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;

public class EntityCastSkillDenizenEvent extends BukkitScriptEvent {

    public ISpigotCharacter character;
    public PlayerSkillContext context;

    public static EntityCastSkillDenizenEvent instance;

    public EntityCastSkillDenizenEvent() {
        this.registerCouldMatcher("<entity> casts skill <'skill'>");
        instance = this;
    }


    @Override
    public boolean matches(ScriptPath path) {
        String cmd = path.eventArgLowerAt(1);
        String arg0 = path.eventArgLowerAt(0);
        String arg2 = path.eventArgLowerAt(2);
        String arg3 = path.eventArgLowerAt(3);
        String attacker = cmd.equals("kills") ? arg0 : arg2.equals("by") ? arg3 : "";
        String target = cmd.equals("kills") ? arg2 : arg0;

     // if (!attacker.isEmpty()) {
     //     if (damager != null) {
     //         if (!cause.asString().equals(attacker) &&
     //                 !tryEntity(projectile, attacker) && !tryEntity(damager, attacker)) {
     //             return false;
     //         }
     //     }
     //     else if (!cause.asString().equals(attacker)) {
     //         return false;
     //     }
     // }

     // if (!tryEntity(entity, target)) {
     //     return false;
     // }

     // if (!runInCheck(path, entity.getLocation())) {
     //     return false;
     // }

        return super.matches(path);
    }

    @Override
    public String getName() {
        return "EntityCastsSkill";
    }

    public ObjectTag getContext(String name) {
        switch (name) {
            case "caster":
                return new CharacterTag(character);
            case "skill_context":
                return new SkillContextTag(context, character);
        }
        return super.getContext(name);
    }

    public ScriptEntryData getScriptEntryData() {
        return new BukkitScriptEntryData(character.getPlayer());
    }


}
