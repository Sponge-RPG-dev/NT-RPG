package cz.neumimto.rpg.sponge.bridges;

import cz.neumimto.rpg.api.entity.PropertyService;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.entity.players.classes.PlayerClassData;
import cz.neumimto.rpg.api.entity.players.leveling.ILevelProgression;
import cz.neumimto.rpg.api.skills.tree.SkillTreeSpecialization;
import cz.neumimto.rpg.sponge.SpongeRpgPlugin;
import cz.neumimto.rpg.sponge.entities.players.SpongeCharacterService;
import me.rojo8399.placeholderapi.Placeholder;
import me.rojo8399.placeholderapi.PlaceholderService;
import me.rojo8399.placeholderapi.Source;
import me.rojo8399.placeholderapi.Token;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Set;

import static cz.neumimto.rpg.api.logging.Log.error;

/**
 * Created by NeumimTo on 25.8.2018.
 */
@Singleton
public class Placeholders {

    @Inject
    private SpongeCharacterService characterService;

    @Inject
    private SpongeRpgPlugin plugin;

    @Inject
    private PropertyService propertyService;

    public void init() {
        Sponge.getServiceManager().provide(PlaceholderService.class).ifPresent(a -> {
            a.loadAll(this, plugin)
                    .stream()
                    .map(builder -> builder.author("NeumimTo").plugin(plugin).version("0.0.1-Test"))
                    .forEach(builder -> {
                        try {
                            builder.buildAndRegister();
                        } catch (Exception e) {
                            error("Could not register placeholder ", e);
                        }
                    });
        });
    }

    @Placeholder(id = "ntrpg_character_class_")
    public Text getClass(@Source Player src, @Token String classType) {
        PlayerClassData classData = characterService.getCharacter(src).getClassByType(classType);
        if (classData == null) {
            return Text.EMPTY;
        }
        return Text.of(classData.getClassDefinition().getPreferedColor(), classData.getClassDefinition().getName());
    }

    @Placeholder(id = "ntrpg_character_name")
    public Text getCharName(@Source Player src) {
        IActiveCharacter character = characterService.getCharacter(src);
        return Text.of(character.getName());
    }


    @Placeholder(id = "ntrpg_character_property_")
    public Float getCharacterProperty(@Source Player src, @Token String property) {
        int idByName = propertyService.getIdByName(property);
        IActiveCharacter character = characterService.getCharacter(src);
        return character.getProperty(idByName);
    }


    @Placeholder(id = "ntrpg_character_class_level_")
    public int getCharClassLevel(@Source Player src, @Token String classType) {
        PlayerClassData classData = characterService.getCharacter(src).getClassByType(classType);
        if (classData == null) {
            return 0;
        }
        return classData.getLevel();
    }

    @Placeholder(id = "ntrpg_character_class_exp_")
    public double getCharClassExp(@Source Player src, @Token String classType) {
        PlayerClassData classData = characterService.getCharacter(src).getClassByType(classType);
        if (classData == null) {
            return 0D;
        }
        return classData.getExperiencesFromLevel();
    }

    @Placeholder(id = "ntrpg_character_class_exptreshold_")
    public double getCharClassExpTreshold(@Source Player src, @Token String classType) {
        PlayerClassData classData = characterService.getCharacter(src).getClassByType(classType);
        if (classData == null) {
            return 0D;
        }
        ILevelProgression levelProgression = classData.getClassDefinition().getLevelProgression();
        if (levelProgression == null) {
            return 0D;
        }
        return levelProgression.getLevelMargins()[classData.getLevel()];
    }

}
