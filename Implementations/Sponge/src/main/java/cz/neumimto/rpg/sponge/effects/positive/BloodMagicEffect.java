

package cz.neumimto.rpg.sponge.effects.positive;

import cz.neumimto.rpg.api.effects.EffectBase;
import cz.neumimto.rpg.api.effects.EffectStatusType;
import cz.neumimto.rpg.api.effects.Generate;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.entity.IEffectConsumer;
import cz.neumimto.rpg.api.entity.IReservable;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.gui.Gui;
import cz.neumimto.rpg.common.effects.core.DefaultManaRegeneration;
import cz.neumimto.rpg.common.entity.players.CharacterMana;

/**
 * Created by ja on 4.9.2015.
 */
@Generate(id = "name", description = "An effect which will redirect all skill's mana consumption to the health pool")
public class BloodMagicEffect extends EffectBase {

    public static String name = "BloodMagic";
    private IActiveCharacter consumer;

    public BloodMagicEffect(IEffectConsumer consumer, long duration) {
        super(name, consumer);
        this.consumer = (IActiveCharacter) consumer;
        setDuration(duration);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void onApply(IEffect self) {
        Gui.sendEffectStatus(consumer, EffectStatusType.APPLIED, this);
        consumer.removeEffect(DefaultManaRegeneration.name);
        IReservable health = (IReservable) consumer.getHealth();
        consumer.setMana(health);
    }


    @Override
    public void onRemove(IEffect self) {
        Gui.sendEffectStatus(consumer, EffectStatusType.EXPIRED, this);
        consumer.setMana(new CharacterMana(consumer));
        //todo re-add mana regain event, or set period of mana regen to long.maxval; + listener
    }

}
