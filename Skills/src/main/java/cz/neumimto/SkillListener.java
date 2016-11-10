package cz.neumimto;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.effects.positive.DamageToMana;
import cz.neumimto.effects.positive.LifeAfterKillEffect;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.effects.IEffect;
import cz.neumimto.rpg.events.character.CharacterDamageEntityEvent;
import cz.neumimto.rpg.players.CharacterService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.IReservable;
import cz.neumimto.rpg.utils.*;
import cz.neumimto.rpg.utils.Utils;
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
                IEffect effect = character.getEffect(LifeAfterKillEffect.name);
                if (effect != null) {
                    LifeAfterKillEffect e = (LifeAfterKillEffect) effect;
                    characterService.healCharacter(character,e.getHealedAmount());
                }
            }
        }
    }

    @Listener
    public void onDamage(CharacterDamageEntityEvent event) {
        IActiveCharacter character = characterService.getCharacter(event.getDamaged().getUniqueId());
        if (!character.isStub()) {
            IEffect effect = character.getEffect(DamageToMana.name);
            if (effect != null) {
                DamageToMana damageToMana = (DamageToMana) effect;
                double value = damageToMana.getValue();
                //todo

            }
        }
    }
}
