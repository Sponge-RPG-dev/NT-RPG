package cz.neumimto.rpg.spigot.bridges.denizen.tags;

import com.denizenscript.denizen.objects.EntityFormObject;
import com.denizenscript.denizen.objects.EntityTag;
import com.denizenscript.denizen.objects.PlayerTag;
import com.denizenscript.denizencore.objects.Fetchable;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.tags.ObjectTagProcessor;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CharacterTag implements EntityFormObject {

    private IActiveCharacter character;
    private String prefix;

    public static ObjectTagProcessor<CharacterTag> tagProcessor = new ObjectTagProcessor<>();

    public CharacterTag(IActiveCharacter character) {
        this.character = character;
    }

    public IActiveCharacter getCharacter() {
        return character;
    }

    public Player player() {
        return (Player) character.getEntity();
    }

    @Override
    public EntityTag getDenizenEntity() {
        return new EntityTag((Entity) character.getEntity());
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public boolean isUnique() {
        return true;
    }

    @Override
    public String getObjectType() {
        return "Character";
    }

    @Override
    public String identify() {
        return "character@" + character.getUUID();
    }

    @Fetchable("character")
    public static CharacterTag valueOf(String string, TagContext context) {
        if (string == null) {
            return null;
        } else {
            if (string.startsWith("character@")) {
                string = string.substring("character@".length());
                UUID uuid = UUID.fromString(string);
                return new CharacterTag(Rpg.get().getCharacterService().getCharacter(uuid));
            }
        }
        return null;
    }

    public static boolean matches(String arg) {
        if (arg.startsWith("character@")) {
            return true;
        } else {
            return valueOf(arg, CoreUtilities.noDebugContext) != null;
        }
    }

    @Override
    public String identifySimple() {
        return this.identify();
    }

    @Override
    public ObjectTag setPrefix(String s) {
        this.prefix = s;
        return this;
    }

    public static void registerTags() {
        tagProcessor.registerTag(PlayerTag.class, "player", (attribute, object) -> new PlayerTag(object.player()));
    }
}
