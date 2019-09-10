package cz.neumimto.rpg.sponge.utils;

import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.entity.players.classes.PlayerClassData;
import cz.neumimto.rpg.api.skills.tree.SkillTreeSpecialization;
import cz.neumimto.rpg.sponge.SpongeRpgPlugin;
import cz.neumimto.rpg.sponge.entities.players.SpongeCharacterService;
import me.rojo8399.placeholderapi.Placeholder;
import me.rojo8399.placeholderapi.PlaceholderService;
import me.rojo8399.placeholderapi.Source;
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


    @Placeholder(id = "class")
    public Text getClass(@Source Player src) {
        PlayerClassData primaryClass = characterService.getCharacter(src).getPrimaryClass();
        return Text.of(primaryClass.getClassDefinition().getPreferedColor(), primaryClass.getClassDefinition().getName());
    }

    @Placeholder(id = "cIhar_name")
    public Text getCharName(@Source Player src) {
        IActiveCharacter character = characterService.getCharacter(src);
        return Text.of(character.getName());
    }

    @Placeholder(id = "primary_class_or_spec")
    public Text getClassOrSpec(@Source Player src) {
        IActiveCharacter character = characterService.getCharacter(src);
        PlayerClassData primaryClass = character.getPrimaryClass();
        Set<SkillTreeSpecialization> skillTreeSpecialization = character.getSkillTreeSpecialization();
        //todo
        return Text.of(primaryClass.getClassDefinition().getPreferedColor(), primaryClass.getClassDefinition().getName());
    }

    @Placeholder(id = "primary_class_level")
    public Integer getPrimaryClassLevel(@Source Player src) {
        IActiveCharacter character = characterService.getCharacter(src);
        return character.getLevel();
    }

    @Placeholder(id = "mana")
    public Double getMana(@Source Player src) {
        IActiveCharacter character = characterService.getCharacter(src);
        return character.getMana().getValue();
    }

    @Placeholder(id = "max_mana")
    public Double getMaxMana(@Source Player src) {
        IActiveCharacter character = characterService.getCharacter(src);
        return character.getMana().getMaxValue();
    }

    @Placeholder(id = "max_hp")
    public Double getMaxHealth(@Source Player src) {
        IActiveCharacter character = characterService.getCharacter(src);
        return character.getHealth().getMaxValue();
    }

}
