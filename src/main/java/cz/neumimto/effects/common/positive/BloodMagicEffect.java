package cz.neumimto.effects.common.positive;

import cz.neumimto.effects.EffectBase;
import cz.neumimto.effects.EffectStatusType;
import cz.neumimto.effects.common.def.ManaRegeneration;
import cz.neumimto.gui.Gui;
import cz.neumimto.players.Health;
import cz.neumimto.players.IActiveCharacter;
import cz.neumimto.players.Mana;

/**
 * Created by ja on 4.9.2015.
 */
public class BloodMagicEffect extends EffectBase {

    public static String name = "BloodMagic Effect";


    @Override
    public String getName() {
        return name;
    }

    private static String apply = "You have gained " + name;
    private static String expire = "You have lost " + name;
    private IActiveCharacter consumer;

    public BloodMagicEffect(IActiveCharacter consumer) {
        super();
        this.consumer = consumer;
        setConsumer(consumer);
        setApplyMessage(apply);
        setExpireMessage(expire);
        setConsumer(consumer);
        setDuration(-1);
    }

    @Override
    public void onApply() {
        Gui.sendEffectStatus(consumer, EffectStatusType.APPLIED, this);
        consumer.removeEffect(ManaRegeneration.class);
        Health health = consumer.getHealth();
        consumer.setMana(health);
    }


    @Override
    public void onRemove() {
        Gui.sendEffectStatus(consumer, EffectStatusType.EXPIRED, this);
        consumer.setMana(new Mana(consumer));
        consumer.addEffect(new ManaRegeneration(consumer));
    }

}
