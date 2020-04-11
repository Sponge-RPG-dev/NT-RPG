package cz.neumimto.skills.passive;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cz.neumimto.effects.positive.PotionEffect;
import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.types.PassiveSkill;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.effect.potion.PotionEffectType;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Singleton;

/**
 * Created by NeumimTo on 20.8.2017.
 */
@Singleton
@ResourceLoader.Skill("ntrpg:potion")
public class SkillPotion extends PassiveSkill {

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public void init() {
        Map<PotionEffectType, Long> list = new HashMap<>();
        Collection<PotionEffectType> allOf = Sponge.getRegistry().getAllOf(PotionEffectType.class);
        for (PotionEffectType type : allOf) {
            list.put(type, 19000L);
        }
        settings.addNode("cooldown-reduced", 0, -125);
        settings.addObjectNode("potions", gson.toJson(list));
    }

    @Override
    public void applyEffect(PlayerSkillContext info, IActiveCharacter character) {
        PotionEffect pe = (PotionEffect) character.getEffect(PotionEffect.name);
        if (pe == null) {
            String potions = info.getSkillData().getSkillSettings().getObjectNode("potions");
            //todo
        }
    }
}
