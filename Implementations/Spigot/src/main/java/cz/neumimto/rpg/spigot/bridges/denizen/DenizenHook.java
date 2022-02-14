package cz.neumimto.rpg.spigot.bridges.denizen;

import com.denizenscript.denizencore.events.ScriptEvent;
import com.denizenscript.denizencore.objects.ObjectFetcher;
import cz.neumimto.rpg.common.skills.SkillConfigLoader;
import cz.neumimto.rpg.common.skills.SkillConfigLoaders;
import cz.neumimto.rpg.spigot.bridges.denizen.tags.CharacterTag;
import cz.neumimto.rpg.spigot.bridges.denizen.tags.SkillContextTag;
import org.bukkit.plugin.Plugin;

public class DenizenHook {

    public static SkillConfigLoader DENIZEN_SCRIPT = new SkillConfigLoader("denizen", DenizenScriptSkillWrapper.class);

    public static void init(Plugin plugin) {
        SkillConfigLoaders.register(DENIZEN_SCRIPT);
        ObjectFetcher.registerWithObjectFetcher(CharacterTag.class, CharacterTag.tagProcessor); // character@
        ObjectFetcher.registerWithObjectFetcher(SkillContextTag.class, SkillContextTag.tagProcessor); // skillContext@
        ScriptEvent.registerScriptEvent(EntityCastSkillDenizenEvent.class);
    }

}
