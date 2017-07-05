package cz.neumimto;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.effects.negative.StunEffect;
import cz.neumimto.effects.positive.Bash;
import cz.neumimto.effects.positive.DamageToMana;
import cz.neumimto.effects.positive.LifeAfterKillEffect;
import cz.neumimto.events.StunApplyEvent;
import cz.neumimto.model.BashModel;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.effects.IEffectContainer;
import cz.neumimto.rpg.entities.EntityService;
import cz.neumimto.rpg.events.INEntityWeaponDamageEvent;
import cz.neumimto.rpg.events.character.CharacterDamageEntityEvent;
import cz.neumimto.rpg.players.CharacterService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.properties.DefaultProperties;
import cz.neumimto.rpg.players.properties.PropertyService;
import cz.neumimto.skills.Basher;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DestructEntityEvent;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by ja on 21.5.2016.
 */
@ResourceLoader.ListenerClass
public class SkillListener {

    @Inject
    private CharacterService characterService;

    @Inject
    private EntityService entityService;

    @Inject
    private EffectService effectService;

    @Inject
    private PropertyService propertyService;

    @Inject
    private Game game;

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
                    entityService.healEntity(character,l);
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

    @Listener
    public void onDamage(INEntityWeaponDamageEvent event) {
        if (event.getSource().hasEffect(Bash.name)) {
            IEffectContainer<BashModel, Bash> effect = event.getSource().getEffect(Bash.name);
            BashModel stackedValue = effect.getStackedValue();
            long time = System.currentTimeMillis();
            float reduced = entityService.getEntityProperty(event.getTarget(), DefaultProperties.cooldown_reduce);
            long cooldown = (long) (reduced * (float) stackedValue.cooldown);
            if (stackedValue.lasttime + cooldown <= time) {
                int rnd = ThreadLocalRandom.current().nextInt(100);
                if (rnd <= stackedValue.chance) {
                    StunEffect stunEffect = new StunEffect(event.getTarget(),stackedValue.stunDuration);
                    if (stackedValue.damage > 0) {
                        event.setDamage(event.getDamage() + stackedValue.damage);
                    }
                    if (!game.getEventManager().post(new StunApplyEvent(event.getSource(), event.getTarget(), stunEffect))) {
                        effectService.addEffect(stunEffect, event.getTarget(), effect);
                        stackedValue.lasttime = time;
                    }
                }
            }
        }
    }

    @Listener
    public void onStunApply(StunApplyEvent event) {
        StunEffect effect = event.getEffect();
        float f = entityService.getEntityProperty(event.getSource(), AdditionalProperties.stun_duration_mult);
        effect.setDuration((long) (f * event.getEffect().getDuration()));
    }
}
