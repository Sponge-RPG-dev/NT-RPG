package cz.neumimto;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.effects.positive.DamageToMana;
import cz.neumimto.effects.positive.LifeAfterKillEffect;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.effects.IEffectContainer;
import cz.neumimto.rpg.events.character.CharacterDamageEntityEvent;
import cz.neumimto.rpg.players.CharacterService;
import cz.neumimto.rpg.players.IActiveCharacter;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DestructEntityEvent;

import java.util.Optional;

/**
 * Created by ja on 21.5.2016.
 */
@ResourceLoader.ListenerClass
public class SkillListener {

    @Inject
    private CharacterService characterService;

    @Listener
    public void onKill(DestructEntityEvent.Death event) {
        Optional<Player> first = event.getCause().first(Player.class);
        if (first.isPresent()) {
            Player player = first.get();
            IActiveCharacter character = characterService.getCharacter(player.getUniqueId());
            if (!character.isStub()) {
                IEffectContainer container = character.getEffect(LifeAfterKillEffect.name);
                if (container != null) {
                    float l = (float) container.getStackedValue();
                    characterService.healCharacter(character,l);
                }
            }
        }
    }

    @Listener
    public void onDamage(CharacterDamageEntityEvent event) {
        IActiveCharacter character = characterService.getCharacter(event.getDamaged().getUniqueId());
        if (!character.isStub()) {
            IEffectContainer container = character.getEffect(DamageToMana.name);
            if (container != null) {
                double percentage = (double) container.getStackedValue();

            }
        }
    }
}
