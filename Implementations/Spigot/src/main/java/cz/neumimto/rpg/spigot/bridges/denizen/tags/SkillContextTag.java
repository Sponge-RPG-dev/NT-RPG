package cz.neumimto.rpg.spigot.bridges.denizen.tags;

import com.denizenscript.denizencore.objects.Fetchable;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.tags.Attribute;
import com.denizenscript.denizencore.tags.ObjectTagProcessor;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;

import java.util.UUID;

public class SkillContextTag implements ObjectTag {

    private PlayerSkillContext context;
    private ActiveCharacter character;
    private String prefix;

    public static ObjectTagProcessor<SkillContextTag> tagProcessor = new ObjectTagProcessor<>();


    public SkillContextTag(PlayerSkillContext context, ActiveCharacter character) {
        this.context = context;
        this.character = character;
    }

    public double getValue(String value) {
        return context.getDoubleNodeValue(value);
    }

    public int getSkillLevel() {
        return context.getTotalLevel();
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
        return "SkillContext";
    }

    @Override
    public String identify() {
        return "skillContext@" + context.getSkill().getId() + "," + character.getUUID();
    }

    @Fetchable("skillContext")
    public static SkillContextTag valueOf(String string, TagContext context) {
        if (string == null) {
            return null;
        } else {
            if (string.startsWith("skillContext@")) {
                string = string.substring("skillContext@".length());
                String[] split = string.split(",");
                UUID uuid = UUID.fromString(split[1]);
                String skillId = split[0];
                ActiveCharacter character = Rpg.get().getCharacterService().getCharacter(uuid);
                PlayerSkillContext psc = character.getSkill(skillId);
                return new SkillContextTag(psc, character);
            }
        }
        return null;
    }

    public static boolean matches(String arg) {
        if (arg.startsWith("skillContext@")) {
            return true;
        } else {
            return valueOf(arg, CoreUtilities.noDebugContext) != null;
        }
    }

    public ObjectTag getObjectAttribute(Attribute attribute) {
        return tagProcessor.getObjectAttribute(this, attribute);
    }

    @Override
    public String identifySimple() {
        return identify();
    }

    @Override
    public ObjectTag setPrefix(String s) {
        prefix = s;
        return this;
    }

    public static void registerTags() {
        tagProcessor.registerTag(ElementTag.class, "value", (attribute, object) -> {
            ElementTag elementTag = new ElementTag(object.context.getDoubleNodeValue(attribute.getRawParam()));
            attribute.fulfill(1);
            return elementTag;
        });
    }
}
